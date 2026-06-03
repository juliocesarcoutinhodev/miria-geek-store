package br.com.miriageekstore.catalog.domain.port.in;

import java.util.UUID;

public record AdjustStockResult(
        UUID variantId,
        String sku,
        int stock
) {}
