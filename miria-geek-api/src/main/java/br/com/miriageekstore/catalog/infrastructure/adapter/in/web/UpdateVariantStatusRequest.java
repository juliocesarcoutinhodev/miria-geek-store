package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import jakarta.validation.constraints.NotNull;

record UpdateVariantStatusRequest(
        @NotNull Boolean ativo
) {}
