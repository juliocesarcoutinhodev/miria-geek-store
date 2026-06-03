package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

record AdminProductDetailResponse(
        UUID id,
        String name,
        String slug,
        String description,
        CategoryInfo category,
        String status,
        boolean featured,
        Instant createdAt,
        List<ImageInfo> images,
        List<VariantInfo> variants
) {
    record CategoryInfo(UUID id, String name) {}

    record ImageInfo(UUID id, String url, boolean principal, int imageOrder) {}

    record VariantInfo(
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
