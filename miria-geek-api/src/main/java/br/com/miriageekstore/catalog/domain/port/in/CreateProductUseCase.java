package br.com.miriageekstore.catalog.domain.port.in;

public interface CreateProductUseCase {
    CreateProductResult execute(CreateProductCommand command);
}
