package br.com.miriageekstore.catalog.domain.model;

import java.time.Instant;
import java.util.UUID;

public record StockMovement(
        UUID id,
        UUID variantId,
        UUID productId,
        StockMovementType type,
        int quantity,
        String motivo,
        UUID adminId,
        Instant createdAt
) {
    public static StockMovement create(UUID variantId, UUID productId, StockMovementType type,
                                        int quantity, String motivo, UUID adminId) {
        return new StockMovement(UUID.randomUUID(), variantId, productId, type,
                quantity, motivo, adminId, Instant.now());
    }
}
