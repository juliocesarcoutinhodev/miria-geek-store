CREATE TABLE categories (
    id          UUID                     NOT NULL PRIMARY KEY,
    name        VARCHAR(100)             NOT NULL,
    slug        VARCHAR(100)             NOT NULL,
    description TEXT                     NOT NULL,
    active      BOOLEAN                  NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT uq_categories_name UNIQUE (name),
    CONSTRAINT uq_categories_slug UNIQUE (slug)
);

CREATE INDEX idx_categories_active ON categories (active);
CREATE INDEX idx_categories_slug   ON categories (slug);
