package com.shrinivas.healthcare_api_service.service;

import com.shrinivas.healthcare_api_service.constant.OutboxConstants;
import com.shrinivas.healthcare_api_service.dto.FileUploadedEventPayload;
import com.shrinivas.healthcare_api_service.enums.FileStatus;
import com.shrinivas.healthcare_api_service.enums.JobStatus;
import com.shrinivas.healthcare_api_service.enums.JobType;
import com.shrinivas.healthcare_api_service.enums.OutboxEventStatus;
import com.shrinivas.healthcare_api_service.model.FileEntity;
import com.shrinivas.healthcare_api_service.model.OutboxEvent;
import com.shrinivas.healthcare_api_service.model.ProcessingJob;
import com.shrinivas.healthcare_api_service.repository.FileRepository;
import com.shrinivas.healthcare_api_service.repository.OutboxEventRepository;
import com.shrinivas.healthcare_api_service.repository.ProcessingJobRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;


@Service
public class FilePersistenceService {

    private final FileRepository fileRepository;
    private final ProcessingJobRepository processingJobRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public FilePersistenceService(
            FileRepository fileRepository,
            ProcessingJobRepository processingJobRepository,
            OutboxEventRepository outboxEventRepository,
            ObjectMapper objectMapper) {

        this.fileRepository = fileRepository;
        this.processingJobRepository = processingJobRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void reserveFile(FileEntity fileEntity) {
        fileRepository.save(fileEntity);
    }

    @Transactional
    public void completeUpload(
            UUID fileId,
            UUID jobId,
            UUID eventId,
            String correlationId,
            JobType jobType,
            FileUploadedEventPayload payload) {

        Instant now = Instant.now();

        // 1. Find and update the file
        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException(
                                "File not found: " + fileId));

        file.setStatus(FileStatus.UPLOADED);
        file.setUploadedAt(now);

        // 2. Create the processing job
        ProcessingJob job = new ProcessingJob();

        job.setId(jobId);
        job.setFile(file);
        job.setJobType(jobType);
        job.setStatus(JobStatus.QUEUED);
        job.setCorrelationId(correlationId);
        job.setRetryCount(0);

        processingJobRepository.save(job);

        // 3. Create the outbox event
        OutboxEvent event = new OutboxEvent();

        event.setId(eventId);
        event.setAggregateType(OutboxConstants.AGGREGATE_FILE);
        event.setAggregateId(fileId);
        event.setEventType(OutboxConstants.EVENT_FILE_UPLOADED);

        Map<String, Object> jsonPayload = objectMapper.convertValue(
                payload,
                new tools.jackson.core.type.TypeReference<Map<String, Object>>() {}
        );
        event.setPayload(jsonPayload);

        event.setStatus(OutboxEventStatus.PENDING);
        event.setRetryCount(0);
        event.setPublishedAt(null);
        event.setLastError(null);
        event.setNextAttemptAt(now);

        outboxEventRepository.save(event);
    }

    @Transactional
    public void markUploadFailed(
            UUID fileId,
            String errorMessage) {

        FileEntity file = fileRepository.findById(fileId).orElse(null);

        if (file != null) {
            file.setStatus(FileStatus.FAILED);
        }

        // Add an appropriate error field to FileEntity
        // if you want to retain the upload failure reason.
    }
}

