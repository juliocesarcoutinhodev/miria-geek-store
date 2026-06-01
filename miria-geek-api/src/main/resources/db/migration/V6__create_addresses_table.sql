CREATE TABLE addresses (
    id              UUID                        NOT NULL,
    user_id         UUID                        NOT NULL,
    alias           VARCHAR(100)                NOT NULL,
    zip_code        VARCHAR(10)                 NOT NULL,
    street          VARCHAR(255)                NOT NULL,
    number          VARCHAR(20)                 NOT NULL,
    complement      VARCHAR(100),
    neighborhood    VARCHAR(100)                NOT NULL,
    city            VARCHAR(100)                NOT NULL,
    state           CHAR(2)                     NOT NULL,
    is_default      BOOLEAN                     NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP WITH TIME ZONE    NOT NULL,
    CONSTRAINT pk_addresses         PRIMARY KEY (id),
    CONSTRAINT fk_addresses_user    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_addresses_user_id ON addresses (user_id);
