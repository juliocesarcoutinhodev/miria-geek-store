package br.com.miriageekstore.catalog.domain.port.in;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CreateProductResult(
        UUID id,
        String name,
        String slug,
        String description,
        UUID categoryId,
        String status,
        boolean featured,
        List<VariantSummary> variants,
        Instant createdAt
) {
    public record VariantSummary(
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
}
