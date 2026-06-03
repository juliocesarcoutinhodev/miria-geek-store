package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

record ProductImageResponse(
        UUID id,
        String url,
        boolean principal,
        int imageOrder,
        Instant createdAt
) {}
