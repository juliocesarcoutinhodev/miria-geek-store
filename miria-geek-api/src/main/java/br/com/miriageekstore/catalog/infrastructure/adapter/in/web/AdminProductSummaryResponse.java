package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

record AdminProductSummaryResponse(
        UUID id,
        String name,
        String slug,
        CategoryInfo category,
        String status,
        boolean featured,
        int totalVariants,
        int totalImages,
        int totalStock,
        Instant createdAt
) {
    record CategoryInfo(UUID id, String name) {}
}
