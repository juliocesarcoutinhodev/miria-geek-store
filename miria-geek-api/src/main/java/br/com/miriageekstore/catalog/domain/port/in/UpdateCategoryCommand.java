package br.com.miriageekstore.catalog.domain.port.in;

import br.com.miriageekstore.catalog.domain.model.CategoryId;

public record UpdateCategoryCommand(
        CategoryId id,
        String name,
        String description,
        boolean active
) {}
