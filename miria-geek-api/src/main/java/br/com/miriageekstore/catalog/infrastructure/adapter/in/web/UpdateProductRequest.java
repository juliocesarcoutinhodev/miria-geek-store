package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

record UpdateProductRequest(
        @NotBlank(message = "O nome é obrigatório")
        String name,

        @NotBlank(message = "A descrição é obrigatória")
        String description,

        @NotNull(message = "A categoria é obrigatória")
        UUID categoryId,

        Boolean featured
) {}
