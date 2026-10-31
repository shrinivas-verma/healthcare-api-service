ALTER TABLE outbox_event
    ADD COLUMN next_attempt_at TIMESTAMP;