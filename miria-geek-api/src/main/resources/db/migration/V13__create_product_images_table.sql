CREATE TABLE product_images (
    id          UUID                     NOT NULL PRIMARY KEY,
    product_id  UUID                     NOT NULL REFERENCES products(id),
    url         TEXT                     NOT NULL,
    filename    VARCHAR(500)             NOT NULL,
    principal   BOOLEAN                  NOT NULL DEFAULT FALSE,
    image_order INTEGER                  NOT NULL DEFAULT 0,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_product_images_product_id ON product_images (product_id);
CREATE INDEX idx_product_images_principal  ON product_images (product_id, principal);
