package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

record CategoryResponse(
        UUID id,
        String name,
        String slug,
        String description,
        long totalProducts,
        boolean active,
        Instant createdAt
) {}
