package br.com.miriageekstore.catalog.domain.model;

import java.util.UUID;

public record VariantId(UUID value) {

    public static VariantId generate() {
        return new VariantId(UUID.randomUUID());
    }

    public static VariantId of(UUID id) {
        return new VariantId(id);
    }
}
