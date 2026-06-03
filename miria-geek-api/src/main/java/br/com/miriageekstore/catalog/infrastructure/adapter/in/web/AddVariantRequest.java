package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

record AddVariantRequest(
        @NotBlank String attributeName,
        @NotBlank String attributeValue,
        @NotNull @Positive BigDecimal price,
        @PositiveOrZero int stock,
        String sku
) {}
