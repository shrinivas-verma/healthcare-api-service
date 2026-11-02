CREATE INDEX idx_processing_job_file_id
    ON processing_job (file_id);

CREATE INDEX idx_processing_job_status_created_at
    ON processing_job (status, created_at);