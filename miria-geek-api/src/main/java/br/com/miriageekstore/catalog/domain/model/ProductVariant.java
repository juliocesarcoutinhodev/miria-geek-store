package br.com.miriageekstore.catalog.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public class ProductVariant {

    private final VariantId id;
    private String attributeName;
    private String attributeValue;
    private BigDecimal price;
    private int stock;
    private Sku sku;
    private boolean active;
    private final Instant createdAt;

    private ProductVariant(VariantId id, String attributeName, String attributeValue,
                           BigDecimal price, int stock, Sku sku, boolean active, Instant createdAt) {
        this.id = id;
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.price = price;
        this.stock = stock;
        this.sku = sku;
        this.active = active;
        this.createdAt = createdAt;
    }

    public static ProductVariant create(String attributeName, String attributeValue,
                                         BigDecimal price, int stock, Sku sku) {
        return new ProductVariant(
                VariantId.generate(), attributeName, attributeValue,
                price, stock, sku, true, Instant.now()
        );
    }

    public static ProductVariant reconstitute(VariantId id, String attributeName, String attributeValue,
                                               BigDecimal price, int stock, Sku sku, boolean active,
                                               Instant createdAt) {
        return new ProductVariant(id, attributeName, attributeValue, price, stock, sku, active, createdAt);
    }

    public void update(String attributeName, String attributeValue, BigDecimal price, int stock, Sku sku) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.price = price;
        this.stock = stock;
        this.sku = sku;
    }

    public void changeStatus(boolean active) {
        this.active = active;
    }

    public void adjustStock(int delta) {
        this.stock += delta;
    }

    public VariantId getId() { return id; }
    public String getAttributeName() { return attributeName; }
    public String getAttributeValue() { return attributeValue; }
    public BigDecimal getPrice() { return price; }
    public int getStock() { return stock; }
    public Sku getSku() { return sku; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
}
