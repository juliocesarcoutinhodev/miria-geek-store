package br.com.miriageekstore.cart.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class CartItem {

    private final CartItemId id;
    private final UUID variantId;
    private int quantity;
    private BigDecimal priceSnapshot;
    private final Instant addedAt;

    private CartItem(CartItemId id, UUID variantId, int quantity, BigDecimal priceSnapshot, Instant addedAt) {
        this.id            = id;
        this.variantId     = variantId;
        this.quantity      = quantity;
        this.priceSnapshot = priceSnapshot;
        this.addedAt       = addedAt;
    }

    public static CartItem create(UUID variantId, int quantity, BigDecimal priceSnapshot) {
        return new CartItem(CartItemId.generate(), variantId, quantity, priceSnapshot, Instant.now());
    }

    public static CartItem reconstitute(CartItemId id, UUID variantId, int quantity,
                                        BigDecimal priceSnapshot, Instant addedAt) {
        return new CartItem(id, variantId, quantity, priceSnapshot, addedAt);
    }

    void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

    void updatePriceSnapshot(BigDecimal priceSnapshot) {
        this.priceSnapshot = priceSnapshot;
    }

    public CartItemId getId()            { return id; }
    public UUID getVariantId()           { return variantId; }
    public int getQuantity()             { return quantity; }
    public BigDecimal getPriceSnapshot() { return priceSnapshot; }
    public Instant getAddedAt()          { return addedAt; }
}
