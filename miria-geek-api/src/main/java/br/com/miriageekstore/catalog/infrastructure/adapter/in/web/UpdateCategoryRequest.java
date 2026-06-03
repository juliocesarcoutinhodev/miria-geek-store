package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

record UpdateCategoryRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String name,

        @NotBlank(message = "A descrição é obrigatória")
        String description,

        @NotNull(message = "O status ativo é obrigatório")
        Boolean active
) {}
