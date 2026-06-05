package br.com.miriageekstore.catalog.domain.port.in;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record UpdateVariantResult(
        UUID id,
        String attributeName,
        String attributeValue,
        BigDecimal price,
        int stock,
        String sku,
        boolean active,
        Instant createdAt,
        BigDecimal weight,
        BigDecimal width,
        BigDecimal height,
        BigDecimal depth
) {}
