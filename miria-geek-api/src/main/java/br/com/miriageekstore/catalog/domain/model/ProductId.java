package br.com.miriageekstore.catalog.domain.model;

import java.util.UUID;

public record ProductId(UUID value) {

    public static ProductId generate() {
        return new ProductId(UUID.randomUUID());
    }

    public static ProductId of(UUID id) {
        return new ProductId(id);
    }
}
