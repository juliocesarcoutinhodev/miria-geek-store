package br.com.miriageekstore.cart.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Cart {

    private final CartId id;
    private final UUID userId;
    private final List<CartItem> items;
    private final Instant createdAt;
    private Instant updatedAt;
    private String selectedShippingOptionId;

    private Cart(CartId id, UUID userId, List<CartItem> items,
                 Instant createdAt, Instant updatedAt, String selectedShippingOptionId) {
        this.id                        = id;
        this.userId                    = userId;
        this.items                     = new ArrayList<>(items);
        this.createdAt                 = createdAt;
        this.updatedAt                 = updatedAt;
        this.selectedShippingOptionId  = selectedShippingOptionId;
    }

    public static Cart create(UUID userId) {
        var now = Instant.now();
        return new Cart(CartId.generate(), userId, new ArrayList<>(), now, now, null);
    }

    public static Cart reconstitute(CartId id, UUID userId, List<CartItem> items,
                                    Instant createdAt, Instant updatedAt,
                                    String selectedShippingOptionId) {
        return new Cart(id, userId, items, createdAt, updatedAt, selectedShippingOptionId);
    }

    public void addItem(UUID variantId, int quantity, java.math.BigDecimal priceSnapshot) {
        var existing = findItemByVariantId(variantId);
        if (existing.isPresent()) {
            existing.get().updateQuantity(existing.get().getQuantity() + quantity);
        } else {
            items.add(CartItem.create(variantId, quantity, priceSnapshot));
        }
        this.selectedShippingOptionId = null;
        this.updatedAt = Instant.now();
    }

    public Optional<CartItem> findItemByVariantId(UUID variantId) {
        return items.stream().filter(i -> i.getVariantId().equals(variantId)).findFirst();
    }

    public CartId getId()                          { return id; }
    public UUID getUserId()                        { return userId; }
    public List<CartItem> getItems()               { return Collections.unmodifiableList(items); }
    public Instant getCreatedAt()                  { return createdAt; }
    public Instant getUpdatedAt()                  { return updatedAt; }
    public String getSelectedShippingOptionId()    { return selectedShippingOptionId; }
}
