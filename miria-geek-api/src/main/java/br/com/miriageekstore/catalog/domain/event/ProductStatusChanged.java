package br.com.miriageekstore.catalog.domain.event;

import java.time.Instant;
import java.util.UUID;

public record ProductStatusChanged(
        UUID productId,
        String name,
        String status,
        Instant occurredAt
) {}
