package br.com.miriageekstore.cart.domain.model;

import java.util.UUID;

public record CartItemId(UUID value) {
    public static CartItemId generate() { return new CartItemId(UUID.randomUUID()); }
    public static CartItemId of(UUID id) { return new CartItemId(id); }
}
