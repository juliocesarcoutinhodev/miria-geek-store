package br.com.miriageekstore.catalog.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public class ProductVariant {

    private final VariantId id;
    private final String attributeName;
    private final String attributeValue;
    private final BigDecimal price;
    private final int stock;
    private final Sku sku;
    private final Instant createdAt;

    private ProductVariant(VariantId id, String attributeName, String attributeValue,
                           BigDecimal price, int stock, Sku sku, Instant createdAt) {
        this.id = id;
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.price = price;
        this.stock = stock;
        this.sku = sku;
        this.createdAt = createdAt;
    }

    public static ProductVariant create(String attributeName, String attributeValue,
                                         BigDecimal price, int stock, Sku sku) {
        return new ProductVariant(
                VariantId.generate(), attributeName, attributeValue,
                price, stock, sku, Instant.now()
        );
    }

    public static ProductVariant reconstitute(VariantId id, String attributeName, String attributeValue,
                                               BigDecimal price, int stock, Sku sku, Instant createdAt) {
        return new ProductVariant(id, attributeName, attributeValue, price, stock, sku, createdAt);
    }

    public VariantId getId() { return id; }
    public String getAttributeName() { return attributeName; }
    public String getAttributeValue() { return attributeValue; }
    public BigDecimal getPrice() { return price; }
    public int getStock() { return stock; }
    public Sku getSku() { return sku; }
    public Instant getCreatedAt() { return createdAt; }
}
