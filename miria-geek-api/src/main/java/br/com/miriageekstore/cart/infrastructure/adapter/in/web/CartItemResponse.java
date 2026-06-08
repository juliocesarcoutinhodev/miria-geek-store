package br.com.miriageekstore.cart.infrastructure.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

record CartItemResponse(
        UUID id,
        UUID variantId,
        UUID productId,
        String productName,
        String attributeName,
        String attributeValue,
        String sku,
        String principalImageUrl,
        int quantity,
        BigDecimal priceSnapshot,
        BigDecimal subtotal,
        int availableStock,
        boolean variantActive,
        Instant addedAt
) {}
