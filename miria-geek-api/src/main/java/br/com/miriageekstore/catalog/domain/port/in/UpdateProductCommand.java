package br.com.miriageekstore.catalog.domain.port.in;

import br.com.miriageekstore.catalog.domain.model.ProductId;

import java.util.UUID;

public record UpdateProductCommand(
        ProductId id,
        String name,
        String description,
        UUID categoryId,
        boolean featured
) {}
