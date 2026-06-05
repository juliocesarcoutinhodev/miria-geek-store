package br.com.miriageekstore.order.domain.model;

import java.util.UUID;

public record OrderId(UUID value) {

    public static OrderId of(UUID id) {
        return new OrderId(id);
    }

    public static OrderId generate() {
        return new OrderId(UUID.randomUUID());
    }
}
