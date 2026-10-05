CREATE TABLE organization (
                              id UUID PRIMARY KEY,
                              name VARCHAR(255) NOT NULL,
                              code VARCHAR(100) NOT NULL,
                              enabled BOOLEAN NOT NULL DEFAULT TRUE,
                              created_at TIMESTAMP NOT NULL,
                              updated_at TIMESTAMP NOT NULL,

                              CONSTRAINT uk_organization_code UNIQUE (code)
);