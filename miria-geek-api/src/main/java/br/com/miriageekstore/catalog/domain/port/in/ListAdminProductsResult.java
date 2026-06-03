package br.com.miriageekstore.catalog.domain.port.in;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ListAdminProductsResult(
        List<AdminProductItem> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public record AdminProductItem(
            UUID id,
            String name,
            String slug,
            UUID categoryId,
            String categoryName,
            String status,
            boolean featured,
            int totalVariants,
            int totalImages,
            int totalStock,
            Instant createdAt
    ) {}
}
