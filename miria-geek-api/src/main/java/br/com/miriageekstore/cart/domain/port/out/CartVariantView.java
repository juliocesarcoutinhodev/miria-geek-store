package br.com.miriageekstore.cart.domain.port.out;

import java.math.BigDecimal;
import java.util.UUID;

public record CartVariantView(
        UUID variantId,
        UUID productId,
        String productName,
        String attributeName,
        String attributeValue,
        String sku,
        String principalImageUrl,
        BigDecimal price,
        int stock,
        boolean variantActive,
        boolean productActive,
        boolean hasDimensions
) {}
