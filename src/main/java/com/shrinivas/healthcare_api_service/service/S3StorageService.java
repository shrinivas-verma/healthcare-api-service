package com.shrinivas.healthcare_api_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
public class S3StorageService {
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public boolean checkBucketAccess() {

        HeadBucketRequest request = HeadBucketRequest.builder()
                .bucket(bucketName)
                .build();

        s3Client.headBucket(request);

        return true;
    }

    public String uploadFile(MultipartFile file, String s3ObjectKey) {

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3ObjectKey)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(
                            file.getInputStream(),
                            file.getSize()
                    )
            );

            return s3ObjectKey;

        } catch (IOException e) {
            throw new RuntimeException("Failed to read uploaded file", e);
        }
    }
}
