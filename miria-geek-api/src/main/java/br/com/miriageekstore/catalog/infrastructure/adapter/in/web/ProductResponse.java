package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

record ProductResponse(
        UUID id,
        String name,
        String slug,
        String description,
        UUID categoryId,
        String status,
        boolean featured,
        List<VariantResponse> variants,
        Instant createdAt
) {
    record VariantResponse(
            UUID id,
            String attributeName,
            String attributeValue,
            BigDecimal price,
            int stock,
            String sku,
            boolean active,
            Instant createdAt
    ) {}
}
