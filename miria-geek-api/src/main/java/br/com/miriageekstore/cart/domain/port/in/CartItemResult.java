package br.com.miriageekstore.cart.domain.port.in;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CartItemResult(
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
        BigDecimal currentPrice,
        BigDecimal subtotal,
        int availableStock,
        boolean variantActive,
        boolean priceChanged,
        boolean insufficientStock,
        BigDecimal weight,
        BigDecimal width,
        BigDecimal height,
        BigDecimal depth,
        Instant addedAt
) {}
