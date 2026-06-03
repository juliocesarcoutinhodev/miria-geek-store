package br.com.miriageekstore.catalog.domain.model;

import java.util.UUID;

public record CategoryId(UUID value) {

    public static CategoryId generate() {
        return new CategoryId(UUID.randomUUID());
    }

    public static CategoryId of(UUID id) {
        return new CategoryId(id);
    }
}
