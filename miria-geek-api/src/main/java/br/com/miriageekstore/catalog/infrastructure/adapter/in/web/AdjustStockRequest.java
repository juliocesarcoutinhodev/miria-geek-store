package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import br.com.miriageekstore.catalog.domain.model.StockMovementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

record AdjustStockRequest(
        @NotNull StockMovementType tipo,
        @Positive int quantidade,
        @NotBlank String motivo
) {}
