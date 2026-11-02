CREATE INDEX idx_outbox_event_status_created_at
    ON outbox_event (status, created_at);