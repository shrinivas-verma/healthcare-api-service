
-- V18__add_uploaded_by_to_file.sql

ALTER TABLE file
    ADD COLUMN uploaded_by_user_id UUID;

ALTER TABLE file
    ADD CONSTRAINT fk_file_uploaded_by
        FOREIGN KEY (uploaded_by_user_id)
            REFERENCES app_user(id);

CREATE INDEX idx_file_uploaded_by_user_id
    ON file(uploaded_by_user_id);
