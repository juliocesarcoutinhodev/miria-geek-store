package br.com.miriageekstore.catalog.domain.port.in;

import br.com.miriageekstore.catalog.domain.model.ProductId;
import br.com.miriageekstore.catalog.domain.model.ProductStatus;

public record UpdateProductStatusCommand(ProductId id, ProductStatus status) {}
