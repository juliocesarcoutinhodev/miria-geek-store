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
    private BigDecimal weight;
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal depth;

    private ProductVariant(VariantId id, String attributeName, String attributeValue,
                           BigDecimal price, int stock, Sku sku, boolean active, Instant createdAt,
                           BigDecimal weight, BigDecimal width, BigDecimal height, BigDecimal depth) {
        this.id = id;
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.price = price;
        this.stock = stock;
        this.sku = sku;
        this.active = active;
        this.createdAt = createdAt;
        this.weight = weight;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public static ProductVariant create(String attributeName, String attributeValue,
                                        BigDecimal price, int stock, Sku sku,
                                        BigDecimal weight, BigDecimal width,
                                        BigDecimal height, BigDecimal depth) {
        return new ProductVariant(
                VariantId.generate(), attributeName, attributeValue,
                price, stock, sku, true, Instant.now(),
                weight, width, height, depth
        );
    }

    public static ProductVariant reconstitute(VariantId id, String attributeName, String attributeValue,
                                              BigDecimal price, int stock, Sku sku, boolean active,
                                              Instant createdAt, BigDecimal weight, BigDecimal width,
                                              BigDecimal height, BigDecimal depth) {
        return new ProductVariant(id, attributeName, attributeValue, price, stock, sku, active, createdAt,
                weight, width, height, depth);
    }

    public void update(String attributeName, String attributeValue, BigDecimal price, int stock, Sku sku,
                       BigDecimal weight, BigDecimal width, BigDecimal height, BigDecimal depth) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.price = price;
        this.stock = stock;
        this.sku = sku;
        this.weight = weight;
        this.width = width;
        this.height = height;
        this.depth = depth;
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
    public BigDecimal getWeight() { return weight; }
    public BigDecimal getWidth() { return width; }
    public BigDecimal getHeight() { return height; }
    public BigDecimal getDepth() { return depth; }
}
