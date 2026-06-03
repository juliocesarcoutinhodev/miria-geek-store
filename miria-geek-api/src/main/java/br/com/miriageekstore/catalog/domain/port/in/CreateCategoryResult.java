package br.com.miriageekstore.catalog.domain.port.in;

import java.time.Instant;
import java.util.UUID;

public record CreateCategoryResult(
        UUID id,
        String name,
        String slug,
        String description,
        boolean active,
        Instant createdAt
) {}
