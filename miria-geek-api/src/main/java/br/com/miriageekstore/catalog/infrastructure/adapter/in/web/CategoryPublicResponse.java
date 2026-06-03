package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import java.util.UUID;

record CategoryPublicResponse(
        UUID id,
        String name,
        String slug,
        long totalProducts
) {}
