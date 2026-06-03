package br.com.miriageekstore.catalog.domain.port.in;

import java.time.Instant;
import java.util.UUID;

public record GetCategoryByIdResult(
        UUID id,
        String name,
        String slug,
        String description,
        long totalProducts,
        boolean active,
        Instant createdAt
) {}
