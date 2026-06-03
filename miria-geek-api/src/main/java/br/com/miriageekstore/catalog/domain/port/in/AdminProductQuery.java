package br.com.miriageekstore.catalog.domain.port.in;

import java.util.UUID;

public record AdminProductQuery(
        String nome,
        UUID categoriaId,
        String status,
        Boolean destaque,
        int page,
        int size,
        String sort
) {}
