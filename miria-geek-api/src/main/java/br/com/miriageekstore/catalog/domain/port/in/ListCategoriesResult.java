package br.com.miriageekstore.catalog.domain.port.in;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ListCategoriesResult(
        List<CategorySummary> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public record CategorySummary(
            UUID id,
            String name,
            String slug,
            String description,
            long totalProducts,
            boolean active,
            Instant createdAt
    ) {}
}
