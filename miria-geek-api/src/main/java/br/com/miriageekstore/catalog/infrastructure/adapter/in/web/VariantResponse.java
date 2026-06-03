package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

record VariantResponse(
        UUID id,
        String attributeName,
        String attributeValue,
        BigDecimal price,
        int stock,
        String sku,
        boolean active,
        Instant createdAt
) {}
