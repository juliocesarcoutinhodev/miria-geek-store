ALTER TABLE product_variants
    ADD COLUMN weight NUMERIC(8, 3),
    ADD COLUMN width  NUMERIC(8, 2),
    ADD COLUMN height NUMERIC(8, 2),
    ADD COLUMN depth  NUMERIC(8, 2);

COMMENT ON COLUMN product_variants.weight IS 'Weight in kg (e.g. 0.350 = 350g)';
COMMENT ON COLUMN product_variants.width  IS 'Width in cm (largura)';
COMMENT ON COLUMN product_variants.height IS 'Height in cm (altura)';
COMMENT ON COLUMN product_variants.depth  IS 'Depth/length in cm (comprimento)';
