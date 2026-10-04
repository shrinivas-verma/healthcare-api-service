CREATE TABLE file (
                      id UUID PRIMARY KEY,
                      original_file_name VARCHAR(255) NOT NULL,
                      stored_file_name VARCHAR(255) NOT NULL,
                      file_type VARCHAR(100) NOT NULL,
                      content_type VARCHAR(255),
                      file_size BIGINT,
                      s3_bucket VARCHAR(255) NOT NULL,
                      s3_object_key VARCHAR(1000) NOT NULL,
                      status VARCHAR(50) NOT NULL,
                      checksum VARCHAR(255),
                      created_at TIMESTAMP NOT NULL,
                      uploaded_at TIMESTAMP
);