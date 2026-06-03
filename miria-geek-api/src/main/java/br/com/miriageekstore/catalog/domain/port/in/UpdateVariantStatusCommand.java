package br.com.miriageekstore.catalog.domain.port.in;

import java.util.UUID;

public record UpdateVariantStatusCommand(
        UUID productId,
        UUID variantId,
        boolean active
) {}
