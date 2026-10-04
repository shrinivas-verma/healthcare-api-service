CREATE TABLE outbox_event (
                              id UUID PRIMARY KEY,
                              aggregate_type VARCHAR(100) NOT NULL,
                              aggregate_id UUID NOT NULL,
                              event_type VARCHAR(100) NOT NULL,
                              payload JSONB NOT NULL,
                              status VARCHAR(50) NOT NULL,
                              retry_count INTEGER NOT NULL DEFAULT 0,
                              created_at TIMESTAMP NOT NULL,
                              published_at TIMESTAMP,
                              last_error TEXT
);