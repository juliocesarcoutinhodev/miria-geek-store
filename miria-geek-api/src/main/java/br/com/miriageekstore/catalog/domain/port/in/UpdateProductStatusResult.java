package br.com.miriageekstore.catalog.domain.port.in;

import java.util.UUID;

public record UpdateProductStatusResult(UUID id, String name, String status) {}
