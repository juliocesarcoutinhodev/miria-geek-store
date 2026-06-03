package br.com.miriageekstore.catalog.domain.event;

import br.com.miriageekstore.catalog.domain.model.StockMovementType;

import java.time.Instant;
import java.util.UUID;

public record StockUpdated(
        UUID productId,
        UUID variantId,
        String sku,
        StockMovementType type,
        int quantity,
        String motivo,
        int newStock,
        UUID adminId,
        Instant occurredAt
) {}
