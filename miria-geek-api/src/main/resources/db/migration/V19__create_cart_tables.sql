CREATE TABLE carts (
    id                          UUID        PRIMARY KEY,
    user_id                     UUID        NOT NULL REFERENCES users(id),
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    selected_shipping_option_id VARCHAR(100),
    CONSTRAINT uq_carts_user_id UNIQUE (user_id)
);

CREATE TABLE cart_items (
    id             UUID           PRIMARY KEY,
    cart_id        UUID           NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    variant_id     UUID           NOT NULL REFERENCES product_variants(id),
    quantity       INTEGER        NOT NULL CHECK (quantity > 0),
    price_snapshot NUMERIC(12, 2) NOT NULL,
    added_at       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_cart_items_cart_variant UNIQUE (cart_id, variant_id)
);
