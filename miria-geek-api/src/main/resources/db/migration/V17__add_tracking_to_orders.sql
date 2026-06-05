ALTER TABLE orders
    ADD COLUMN tracking_code  VARCHAR(50),
    ADD COLUMN carrier        VARCHAR(50),
    ADD COLUMN carrier_name   VARCHAR(100),
    ADD COLUMN tracking_url   VARCHAR(500);
