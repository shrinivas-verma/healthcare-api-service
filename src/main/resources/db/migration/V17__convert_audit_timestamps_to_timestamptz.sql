-- FILE
ALTER TABLE file
ALTER COLUMN created_at TYPE TIMESTAMPTZ
        USING created_at AT TIME ZONE 'Asia/Kolkata',
    ALTER COLUMN uploaded_at TYPE TIMESTAMPTZ
        USING uploaded_at AT TIME ZONE 'Asia/Kolkata',
    ALTER COLUMN updated_at TYPE TIMESTAMPTZ
        USING updated_at AT TIME ZONE 'Asia/Kolkata';


-- OUTBOX EVENT
ALTER TABLE outbox_event
ALTER COLUMN created_at TYPE TIMESTAMPTZ
        USING created_at AT TIME ZONE 'Asia/Kolkata',
    ALTER COLUMN published_at TYPE TIMESTAMPTZ
        USING published_at AT TIME ZONE 'Asia/Kolkata',
    ALTER COLUMN next_attempt_at TYPE TIMESTAMPTZ
        USING next_attempt_at AT TIME ZONE 'Asia/Kolkata';


-- PROCESSING JOB
ALTER TABLE processing_job
ALTER COLUMN created_at TYPE TIMESTAMPTZ
        USING created_at AT TIME ZONE 'Asia/Kolkata',
    ALTER COLUMN started_at TYPE TIMESTAMPTZ
        USING started_at AT TIME ZONE 'Asia/Kolkata',
    ALTER COLUMN completed_at TYPE TIMESTAMPTZ
        USING completed_at AT TIME ZONE 'Asia/Kolkata';


-- APP USER
ALTER TABLE app_user
ALTER COLUMN created_at TYPE TIMESTAMPTZ
        USING created_at AT TIME ZONE 'Asia/Kolkata',
    ALTER COLUMN updated_at TYPE TIMESTAMPTZ
        USING updated_at AT TIME ZONE 'Asia/Kolkata';


-- ORGANIZATION
ALTER TABLE organization
ALTER COLUMN created_at TYPE TIMESTAMPTZ
        USING created_at AT TIME ZONE 'Asia/Kolkata',
    ALTER COLUMN updated_at TYPE TIMESTAMPTZ
        USING updated_at AT TIME ZONE 'Asia/Kolkata';