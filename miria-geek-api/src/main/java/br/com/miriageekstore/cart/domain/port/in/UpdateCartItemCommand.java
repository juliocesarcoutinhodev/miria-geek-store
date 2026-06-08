package br.com.miriageekstore.cart.domain.port.in;

import java.util.UUID;

public record UpdateCartItemCommand(UUID userId, UUID itemId, int quantity) {}
