ALTER TABLE product_variants
    ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;

CREATE TABLE stock_movements
(
    id         UUID        NOT NULL,
    variant_id UUID        NOT NULL REFERENCES product_variants (id),
    product_id UUID        NOT NULL REFERENCES products (id),
    type       VARCHAR(20) NOT NULL,
    quantity   INTEGER     NOT NULL,
    motivo     TEXT,
    admin_id   UUID        NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT pk_stock_movements PRIMARY KEY (id)
);

CREATE INDEX idx_stock_movements_variant_id ON stock_movements (variant_id);
CREATE INDEX idx_stock_movements_product_id ON stock_movements (product_id);
