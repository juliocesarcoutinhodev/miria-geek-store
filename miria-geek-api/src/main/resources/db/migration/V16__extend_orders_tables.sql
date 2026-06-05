-- order_items: snapshot columns captured at purchase time
ALTER TABLE order_items
    ADD COLUMN sku             VARCHAR(200),
    ADD COLUMN principal_image VARCHAR(500);

-- orders: delivery address snapshot reference
ALTER TABLE orders
    ADD COLUMN delivery_address_id UUID REFERENCES addresses (id) ON DELETE SET NULL;

CREATE INDEX idx_orders_delivery_address ON orders (delivery_address_id);

-- payments
CREATE TABLE payments (
    id             UUID                     NOT NULL,
    order_id       UUID                     NOT NULL,
    status         VARCHAR(30)              NOT NULL,
    payment_method VARCHAR(50),
    amount         DECIMAL(10, 2)           NOT NULL,
    payment_date   TIMESTAMP WITH TIME ZONE,
    gateway        VARCHAR(100),
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_payments       PRIMARY KEY (id),
    CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
);

CREATE INDEX idx_payments_order_id ON payments (order_id);

-- order_status_history
CREATE TABLE order_status_history (
    id         UUID                     NOT NULL,
    order_id   UUID                     NOT NULL,
    status     VARCHAR(30)              NOT NULL,
    note       TEXT,
    admin_id   UUID,
    changed_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_order_status_history       PRIMARY KEY (id),
    CONSTRAINT fk_order_status_history_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_order_status_history_admin FOREIGN KEY (admin_id) REFERENCES users (id) ON DELETE SET NULL
);

CREATE INDEX idx_order_status_history_order      ON order_status_history (order_id);
CREATE INDEX idx_order_status_history_changed_at ON order_status_history (changed_at ASC);
