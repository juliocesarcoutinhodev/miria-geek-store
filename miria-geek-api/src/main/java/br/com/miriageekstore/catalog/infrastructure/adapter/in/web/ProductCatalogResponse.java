package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import java.math.BigDecimal;
import java.util.UUID;

record ProductCatalogResponse(
        UUID id,
        String nome,
        String slug,
        CategoriaInfo categoria,
        String imagemPrincipal,
        BigDecimal precoMinimo,
        BigDecimal precoMaximo,
        int totalVariantesAtivas,
        boolean destaque
) {
    record CategoriaInfo(UUID id, String nome) {}
}
