package com.shrinivas.healthcare_api_service.service;

import com.shrinivas.healthcare_api_service.constant.OutboxConstants;
import com.shrinivas.healthcare_api_service.dto.FileUploadedEventPayload;
import com.shrinivas.healthcare_api_service.dto.response.FileUploadResponse;
import com.shrinivas.healthcare_api_service.enums.FileStatus;
import com.shrinivas.healthcare_api_service.enums.JobStatus;
import com.shrinivas.healthcare_api_service.enums.JobType;
import com.shrinivas.healthcare_api_service.model.FileEntity;
import com.shrinivas.healthcare_api_service.model.auth.AppUser;
import com.shrinivas.healthcare_api_service.model.auth.Organization;
import com.shrinivas.healthcare_api_service.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;


@Service
public class FileUploadService {

    private final AppUserRepository appUserRepository;
    private final S3StorageService s3StorageService;
    private final FilePersistenceService filePersistenceService;
//    private final ObjectMapper objectMapper;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public FileUploadService(
            AppUserRepository appUserRepository,
            S3StorageService s3StorageService,
            FilePersistenceService filePersistenceService,
            ObjectMapper objectMapper) {

        this.appUserRepository = appUserRepository;
        this.s3StorageService = s3StorageService;
        this.filePersistenceService = filePersistenceService;
//        this.objectMapper = objectMapper;
    }


    public FileUploadResponse receiveFile(
            MultipartFile file,
            JobType jobType) {

        // 1. Validate the request
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        if (jobType == null) {
            throw new IllegalArgumentException("Job type is required");
        }

        // 2. Get authenticated user
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserPrincipal)) {

            throw new AccessDeniedException("Authenticated user not found");
        }

        String username = authentication.getName();

        AppUser appUser = appUserRepository
                .findByUsernameWithOrganization(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(username + " not found"));

        Organization organization = appUser.getOrganization();

        if (organization == null || !organization.getEnabled()) {
            throw new AccessDeniedException("User organization is not active");
        }

        // 3. Generate IDs
        UUID fileId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        String correlationId = UUID.randomUUID().toString();

        // 4. Get file metadata
        String originalFileName =
                sanitizeFileName(file.getOriginalFilename());

        String contentType = file.getContentType();
        String extension = getFileExtension(originalFileName);

        if (!"xlsx".equalsIgnoreCase(extension)) {
            throw new IllegalArgumentException(
                    "Only XLSX files are currently supported");
        }

        if (contentType == null) {
            contentType =
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }

        // 5. Generate S3 object key
        Instant now = Instant.now();

        ZonedDateTime utcDateTime = now.atZone(ZoneOffset.UTC);
        int year = utcDateTime.getYear();
        int month = utcDateTime.getMonthValue();

        String objectKey = "organizations/" + organization.getId()
                + "/files/" + year + "/" + month
                + "/" + fileId + "." + extension;

        Path tempFile = null;

        // Track which operations completed successfully.
        boolean fileReserved = false;
        boolean s3UploadCompleted = false;

        try {

            // 6. Create temporary file
            tempFile = Files.createTempFile(
                    "healthcare-upload-", "." + extension);

            file.transferTo(tempFile.toFile());

            // 7. Calculate SHA-256 checksum
            String checksum = calculateSha256(tempFile);

            // 8. Create initial FileEntity
            FileEntity fileEntity = new FileEntity();

            fileEntity.setId(fileId);
            fileEntity.setOriginalFileName(originalFileName);
            fileEntity.setFileType(extension.toUpperCase());
            fileEntity.setContentType(contentType);
            fileEntity.setFileSize(Files.size(tempFile));
            fileEntity.setS3Bucket(bucketName);
            fileEntity.setS3ObjectKey(objectKey);
            fileEntity.setStatus(FileStatus.UPLOAD_INITIATED);
            fileEntity.setChecksum(checksum);
            fileEntity.setUploadedAt(null);
            fileEntity.setOrganization(organization);
            fileEntity.setUploadedBy(appUser);

            // This method has its own transaction.
            // The transaction commits before this call returns.
            filePersistenceService.reserveFile(fileEntity);
            fileReserved = true;

            // 9. Upload the actual file to S3
            s3StorageService.uploadFile(
                    tempFile, objectKey, contentType);

            s3UploadCompleted = true;

            // 10. Update file and create job + outbox event
            Instant uploadedAt = Instant.now();

            FileUploadedEventPayload payload =
                    new FileUploadedEventPayload(
                            eventId,
                            OutboxConstants.EVENT_FILE_UPLOADED,
                            1,
                            organization.getId(),
                            fileId,
                            jobId,
                            correlationId,
                            s3StorageService.getBucketName(),
                            objectKey,
                            originalFileName,
                            Files.size(tempFile),
                            contentType,
                            checksum,
                            jobType,
                            uploadedAt
                    );

            filePersistenceService.completeUpload(
                    fileId,
                    jobId,
                    eventId,
                    correlationId,
                    jobType,
                    payload
            );

            // 11. Return successful response
            return new FileUploadResponse(
                    fileId,
                    jobId,
                    correlationId,
                    originalFileName,
                    FileStatus.UPLOADED,
                    JobStatus.QUEUED,
                    uploadedAt
            );

        } catch (Exception exception) {

            // 12. If S3 upload completed, attempt to delete the object.
            if (s3UploadCompleted) {
                try {
                    s3StorageService.deleteFile(objectKey);
                } catch (Exception deleteException) {
                    exception.addSuppressed(deleteException);
                }
            }

            // 13. Mark the database record failed,
            // but only if the initial reservation succeeded.
            if (fileReserved) {
                try {
                    filePersistenceService.markUploadFailed(
                            fileId,
                            exception.getMessage()
                    );
                } catch (Exception persistenceException) {
                    exception.addSuppressed(persistenceException);
                }
            }

            throw new RuntimeException(
                    "File upload failed", exception);

        } finally {

            // 14. Delete temporary file regardless of success or failure.
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException exception) {
                    // Log temporary file cleanup failure.
                }
            }
        }
    }


    private String sanitizeFileName(String fileName) {

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("Original file name is required");
        }

        // Remove any path supplied by the client.
        String sanitized = fileName.replace('\\', '/');
        sanitized = sanitized.substring(sanitized.lastIndexOf('/') + 1);

        if (sanitized.isBlank()) {
            throw new IllegalArgumentException("Invalid file name");
        }

        return sanitized;
    }

    private String getFileExtension(String fileName) {

        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            throw new IllegalArgumentException("File extension is missing");
        }

        return fileName.substring(dotIndex + 1);
    }

    private String calculateSha256(Path path) throws Exception {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        try (InputStream inputStream = Files.newInputStream(path)) {

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }

        byte[] hash = digest.digest();

        StringBuilder hexString = new StringBuilder();

        for (byte b : hash) {
            hexString.append(String.format("%02x", b & 0xff));
        }

        return hexString.toString();
    }
}
