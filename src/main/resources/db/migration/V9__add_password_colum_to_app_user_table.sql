-- V2__add_password_column_to_app_user.sql

ALTER TABLE app_user
    ADD COLUMN password VARCHAR(255) NOT NULL;