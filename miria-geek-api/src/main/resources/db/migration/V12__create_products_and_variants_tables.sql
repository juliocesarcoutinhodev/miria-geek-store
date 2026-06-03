CREATE TABLE products (
    id          UUID                     NOT NULL PRIMARY KEY,
    name        VARCHAR(255)             NOT NULL,
    slug        VARCHAR(255)             NOT NULL,
    description TEXT                     NOT NULL,
    category_id UUID                     NOT NULL REFERENCES categories(id),
    status      VARCHAR(20)              NOT NULL,
    featured    BOOLEAN                  NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT uq_products_slug UNIQUE (slug)
);

CREATE TABLE product_variants (
    id              UUID           NOT NULL PRIMARY KEY,
    product_id      UUID           NOT NULL REFERENCES products(id),
    attribute_name  VARCHAR(100)   NOT NULL,
    attribute_value VARCHAR(100)   NOT NULL,
    price           NUMERIC(12, 2) NOT NULL,
    stock           INTEGER        NOT NULL DEFAULT 0,
    sku             VARCHAR(200)   NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT uq_product_variants_sku UNIQUE (sku)
);

CREATE INDEX idx_products_category_id       ON products (category_id);
CREATE INDEX idx_products_status            ON products (status);
CREATE INDEX idx_products_slug              ON products (slug);
CREATE INDEX idx_product_variants_product_id ON product_variants (product_id);
CREATE INDEX idx_product_variants_sku       ON product_variants (sku);
