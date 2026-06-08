package br.com.miriageekstore.cart.domain.model;

import java.util.UUID;

public record CartId(UUID value) {
    public static CartId generate() { return new CartId(UUID.randomUUID()); }
    public static CartId of(UUID id) { return new CartId(id); }
}
