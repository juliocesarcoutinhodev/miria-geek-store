package br.com.miriageekstore.catalog.domain.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSearchQuery(
        String nome,
        UUID categoriaId,
        BigDecimal precoMin,
        BigDecimal precoMax,
        Boolean destaque,
        int page,
        int size,
        String sort
) {}
