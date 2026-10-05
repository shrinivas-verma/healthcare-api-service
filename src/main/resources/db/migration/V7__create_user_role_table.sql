CREATE TABLE user_role (
                           id UUID PRIMARY KEY,
                           user_id UUID NOT NULL,
                           role_id UUID NOT NULL,

                           CONSTRAINT fk_user_role_user
                               FOREIGN KEY (user_id)
                                   REFERENCES app_user(id),

                           CONSTRAINT fk_user_role_role
                               FOREIGN KEY (role_id)
                                   REFERENCES role(id),

                           CONSTRAINT uk_user_role
                               UNIQUE (user_id, role_id)
);