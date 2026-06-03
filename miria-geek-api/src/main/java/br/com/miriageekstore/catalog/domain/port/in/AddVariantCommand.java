package br.com.miriageekstore.catalog.domain.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public record AddVariantCommand(
        UUID productId,
        String attributeName,
        String attributeValue,
        BigDecimal price,
        int stock,
        String sku
) {}
