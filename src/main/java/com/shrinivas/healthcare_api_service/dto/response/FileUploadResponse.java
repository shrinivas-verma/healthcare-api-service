package com.shrinivas.healthcare_api_service.dto.response;

import com.shrinivas.healthcare_api_service.enums.FileStatus;
import com.shrinivas.healthcare_api_service.enums.JobStatus;

import java.time.Instant;
import java.util.UUID;


public record FileUploadResponse(
        UUID fileId,
        UUID jobId,
        String correlationId,
        String originalFileName,
        FileStatus fileStatus,
        JobStatus jobStatus,
        Instant uploadedAt
) {
}
