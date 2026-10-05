
ALTER TABLE file
    ADD COLUMN organization_id UUID NOT NULL;

ALTER TABLE file
    ADD CONSTRAINT fk_file_organization
        FOREIGN KEY (organization_id)
            REFERENCES organization(id);