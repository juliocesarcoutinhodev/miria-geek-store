package br.com.miriageekstore.catalog.domain.port.in;

import java.time.Instant;
import java.util.UUID;

public record ProductImageResult(
        UUID id,
        String url,
        boolean principal,
        int imageOrder,
        Instant createdAt
) {}
