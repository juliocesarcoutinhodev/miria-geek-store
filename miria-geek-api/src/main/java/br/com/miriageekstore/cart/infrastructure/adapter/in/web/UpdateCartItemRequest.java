package br.com.miriageekstore.cart.infrastructure.adapter.in.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

record UpdateCartItemRequest(
        @NotNull @Min(0) Integer quantity
) {}
