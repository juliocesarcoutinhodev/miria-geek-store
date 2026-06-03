package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import java.util.UUID;

record PatchProductRequest(
        String name,
        String description,
        UUID categoryId,
        Boolean featured
) {}
