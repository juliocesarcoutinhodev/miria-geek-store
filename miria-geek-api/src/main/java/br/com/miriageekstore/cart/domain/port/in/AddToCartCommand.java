package br.com.miriageekstore.cart.domain.port.in;

import java.util.UUID;

public record AddToCartCommand(UUID userId, UUID variantId, int quantity) {}
