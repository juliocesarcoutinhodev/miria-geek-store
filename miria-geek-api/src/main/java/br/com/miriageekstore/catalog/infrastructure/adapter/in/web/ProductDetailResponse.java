package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

record ProductDetailResponse(
        UUID id,
        String nome,
        String slug,
        String descricao,
        CategoriaInfo categoria,
        String status,
        boolean destaque,
        Instant dataCriacao,
        List<ImagemInfo> imagens,
        List<VarianteInfo> variantes
) {
    record CategoriaInfo(UUID id, String nome) {}

    record ImagemInfo(UUID id, String url, boolean principal, int ordem) {}

    record VarianteInfo(
            UUID id,
            String nomeAtributo,
            String valorAtributo,
            BigDecimal preco,
            int estoque,
            String sku,
            boolean ativo,
            boolean disponivel
    ) {}
}
