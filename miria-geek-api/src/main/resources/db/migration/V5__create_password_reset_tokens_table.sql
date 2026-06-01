CREATE TABLE password_reset_tokens (
    id          UUID                        NOT NULL,
    user_id     UUID                        NOT NULL,
    token       UUID                        NOT NULL,
    expires_at  TIMESTAMP WITH TIME ZONE    NOT NULL,
    used        BOOLEAN                     NOT NULL DEFAULT FALSE,
    used_at     TIMESTAMP WITH TIME ZONE,
    created_at  TIMESTAMP WITH TIME ZONE    NOT NULL,
    CONSTRAINT pk_password_reset_tokens     PRIMARY KEY (id),
    CONSTRAINT uk_password_reset_token      UNIQUE (token),
    CONSTRAINT fk_password_reset_user       FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_prt_token   ON password_reset_tokens (token);
CREATE INDEX idx_prt_user_id ON password_reset_tokens (user_id);
