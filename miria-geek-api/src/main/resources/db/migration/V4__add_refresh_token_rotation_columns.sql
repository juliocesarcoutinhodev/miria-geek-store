ALTER TABLE refresh_tokens
    ADD COLUMN IF NOT EXISTS family_id  UUID                        NOT NULL DEFAULT gen_random_uuid(),
    ADD COLUMN IF NOT EXISTS revoked    BOOLEAN                     NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS revoked_at TIMESTAMP WITH TIME ZONE;

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_family_id ON refresh_tokens (family_id);
