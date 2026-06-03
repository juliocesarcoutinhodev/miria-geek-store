package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import br.com.miriageekstore.catalog.domain.model.ProductStatus;
import jakarta.validation.constraints.NotNull;

record UpdateProductStatusRequest(
        @NotNull(message = "O status é obrigatório")
        ProductStatus status
) {}
