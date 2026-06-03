package br.com.miriageekstore.catalog.domain.event;

import java.time.Instant;
import java.util.UUID;

public record ProductUpdated(
        UUID productId,
        String name,
        String slug,
        UUID categoryId,
        boolean featured,
        Instant occurredAt
) {}
