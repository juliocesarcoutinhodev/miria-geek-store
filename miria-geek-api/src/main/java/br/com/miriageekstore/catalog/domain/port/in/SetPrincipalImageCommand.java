package br.com.miriageekstore.catalog.domain.port.in;

import java.util.UUID;

public record SetPrincipalImageCommand(UUID productId, UUID imageId) {}
