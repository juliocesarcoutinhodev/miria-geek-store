ALTER TABLE refresh_tokens
    ADD COLUMN family_id  UUID                     NOT NULL DEFAULT gen_random_uuid(),
    ADD COLUMN revoked    BOOLEAN                  NOT NULL DEFAULT FALSE,
    ADD COLUMN revoked_at TIMESTAMP WITH TIME ZONE;

CREATE INDEX idx_refresh_tokens_family_id ON refresh_tokens (family_id);
