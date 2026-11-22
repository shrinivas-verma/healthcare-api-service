package com.shrinivas.healthcare_api_service.dto;


import com.shrinivas.healthcare_api_service.enums.JobType;

import java.time.Instant;
import java.util.UUID;

public record FileUploadedEventPayload(
        UUID eventId,
        String eventType,
        int schemaVersion,

        UUID organizationId,
        UUID fileId,
        UUID jobId,
        String correlationId,

        String bucket,
        String objectKey,

        String originalFileName,
        long fileSize,
        String contentType,
        String checksum,

        JobType jobType,
        Instant occurredAt
) {
}

