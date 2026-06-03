package br.com.miriageekstore.catalog.domain.port.in;

import br.com.miriageekstore.catalog.domain.model.ProductId;

import java.util.UUID;

public record PatchProductCommand(
        ProductId id,
        String name,
        String description,
        UUID categoryId,
        Boolean featured
) {}
