package br.com.miriageekstore.cart.infrastructure.adapter.in.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

record AddToCartRequest(
        @NotNull UUID variantId,
        @Min(1) int quantity
) {}
