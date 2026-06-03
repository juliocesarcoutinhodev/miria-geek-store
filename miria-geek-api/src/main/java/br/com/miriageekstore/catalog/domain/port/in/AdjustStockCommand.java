package br.com.miriageekstore.catalog.domain.port.in;

import br.com.miriageekstore.catalog.domain.model.StockMovementType;

import java.util.UUID;

public record AdjustStockCommand(
        UUID productId,
        UUID variantId,
        StockMovementType type,
        int quantity,
        String motivo,
        UUID adminId
) {}
