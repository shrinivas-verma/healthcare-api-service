CREATE TABLE role (
                      id UUID PRIMARY KEY,
                      name VARCHAR(100) NOT NULL UNIQUE,
                      description VARCHAR(500),

                        CONSTRAINT uk_role_name UNIQUE (name)

);