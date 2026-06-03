package br.com.miriageekstore.catalog.domain.port.in;

public record ListCategoriesQuery(
        String name,
        Boolean active,
        int page,
        int size,
        String sort,
        String direction
) {}
