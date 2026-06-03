package br.com.miriageekstore.catalog.domain.model;

import java.util.UUID;

public record ImageId(UUID value) {

    public static ImageId generate() {
        return new ImageId(UUID.randomUUID());
    }

    public static ImageId of(UUID id) {
        return new ImageId(id);
    }
}
