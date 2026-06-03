package br.com.miriageekstore.catalog.domain.port.in;

import java.util.List;
import java.util.UUID;

public record ListPublicCategoriesResult(List<PublicCategorySummary> categories) {

    public record PublicCategorySummary(
            UUID id,
            String name,
            String slug,
            long totalProducts
    ) {}
}
