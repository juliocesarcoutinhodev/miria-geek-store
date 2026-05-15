CREATE TABLE refresh_tokens (
    id          UUID                        NOT NULL,
    user_id     UUID                        NOT NULL,
    token_hash  VARCHAR(255)                NOT NULL,
    expires_at  TIMESTAMP WITH TIME ZONE    NOT NULL,
    ip_address  VARCHAR(45),
    user_agent  VARCHAR(512),
    created_at  TIMESTAMP WITH TIME ZONE    NOT NULL,
    CONSTRAINT pk_refresh_tokens            PRIMARY KEY (id),
    CONSTRAINT uk_refresh_tokens_hash       UNIQUE (token_hash),
    CONSTRAINT fk_refresh_tokens_user       FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_user_id    ON refresh_tokens (user_id);
CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens (token_hash);
