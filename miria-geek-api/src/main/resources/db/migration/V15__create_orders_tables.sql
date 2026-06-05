CREATE SEQUENCE orders_order_number_seq START 1;

CREATE TABLE orders (
    id           UUID                     NOT NULL,
    order_number VARCHAR(20)              NOT NULL,
    status       VARCHAR(30)              NOT NULL,
    user_id      UUID                     NOT NULL,
    total        DECIMAL(10, 2)           NOT NULL DEFAULT 0,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_orders              PRIMARY KEY (id),
    CONSTRAINT uk_orders_order_number UNIQUE (order_number),
    CONSTRAINT fk_orders_user         FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE order_items (
    id              UUID           NOT NULL,
    order_id        UUID           NOT NULL,
    product_id      UUID,
    variant_id      UUID,
    product_name    VARCHAR(255)   NOT NULL,
    attribute_name  VARCHAR(100),
    attribute_value VARCHAR(100),
    quantity        INTEGER        NOT NULL,
    unit_price      DECIMAL(10, 2) NOT NULL,
    subtotal        DECIMAL(10, 2) NOT NULL,
    CONSTRAINT pk_order_items       PRIMARY KEY (id),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
);

CREATE OR REPLACE FUNCTION orders_set_order_number()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.order_number := 'ORD-' || LPAD(nextval('orders_order_number_seq')::TEXT, 8, '0');
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_orders_order_number
    BEFORE INSERT ON orders
    FOR EACH ROW
    WHEN (NEW.order_number IS NULL OR NEW.order_number = '')
    EXECUTE FUNCTION orders_set_order_number();

CREATE INDEX idx_orders_status      ON orders (status);
CREATE INDEX idx_orders_user_id     ON orders (user_id);
CREATE INDEX idx_orders_created_at  ON orders (created_at DESC);
CREATE INDEX idx_order_items_order  ON order_items (order_id);
