CREATE TABLE app_user (
                          id UUID PRIMARY KEY,
                          username VARCHAR(100) NOT NULL UNIQUE,
                          email VARCHAR(255) NOT NULL UNIQUE,
                          enabled BOOLEAN NOT NULL DEFAULT TRUE,

                          organization_id UUID NOT NULL,

                          created_at TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP NOT NULL,

                          CONSTRAINT fk_app_user_organization
                              FOREIGN KEY (organization_id)
                                  REFERENCES organization(id),

                          CONSTRAINT uk_app_user_username UNIQUE (username),
                          CONSTRAINT uk_app_user_email UNIQUE (email)

);