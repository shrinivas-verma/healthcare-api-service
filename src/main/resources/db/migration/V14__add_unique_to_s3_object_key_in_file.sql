ALTER TABLE file
    ADD CONSTRAINT uk_file_s3_object_key
        UNIQUE (s3_object_key);