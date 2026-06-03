package br.com.miriageekstore.catalog.domain.port.in;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ListProductsResult(
        List<ProductItem> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public record ProductItem(
            UUID id,
            String name,
            String slug,
            UUID categoryId,
            String categoryName,
            String principalImageUrl,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int totalActiveVariants,
            boolean featured
    ) {}
}
