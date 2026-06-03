package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import java.util.UUID;

record AdjustStockResponse(
        UUID variantId,
        String sku,
        int stock
) {}
