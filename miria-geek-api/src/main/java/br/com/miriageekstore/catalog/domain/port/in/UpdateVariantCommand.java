package br.com.miriageekstore.catalog.domain.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateVariantCommand(
        UUID productId,
        UUID variantId,
        String attributeName,
        String attributeValue,
        BigDecimal price,
        int stock,
        String sku,
        BigDecimal weight,
        BigDecimal width,
        BigDecimal height,
        BigDecimal depth
) {}
