CREATE TABLE users (
    id                              UUID                        NOT NULL,
    full_name                       VARCHAR(255)                NOT NULL,
    email                           VARCHAR(255)                NOT NULL,
    password                        VARCHAR(255)                NOT NULL,
    status                          VARCHAR(50)                 NOT NULL,
    verification_token              UUID,
    verification_token_expires_at   TIMESTAMP WITH TIME ZONE,
    created_at                      TIMESTAMP WITH TIME ZONE    NOT NULL,
    CONSTRAINT pk_users             PRIMARY KEY (id),
    CONSTRAINT uk_users_email       UNIQUE (email)
);

CREATE TABLE user_roles (
    user_id     UUID        NOT NULL,
    role        VARCHAR(50) NOT NULL,
    CONSTRAINT pk_user_roles    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_users_email              ON users (email);
CREATE INDEX idx_users_status             ON users (status);
CREATE INDEX idx_users_verification_token ON users (verification_token);
