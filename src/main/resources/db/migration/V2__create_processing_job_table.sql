CREATE TABLE processing_job (
                                id UUID PRIMARY KEY,
                                file_id UUID NOT NULL,
                                job_type VARCHAR(100) NOT NULL,
                                status VARCHAR(50) NOT NULL,
                                priority VARCHAR(50),
                                correlation_id VARCHAR(255) NOT NULL,
                                retry_count INTEGER NOT NULL DEFAULT 0,
                                error_code VARCHAR(100),
                                error_message TEXT,
                                created_at TIMESTAMP NOT NULL,
                                started_at TIMESTAMP,
                                completed_at TIMESTAMP,

                                CONSTRAINT fk_processing_job_file
                                    FOREIGN KEY (file_id)
                                        REFERENCES file(id)
);