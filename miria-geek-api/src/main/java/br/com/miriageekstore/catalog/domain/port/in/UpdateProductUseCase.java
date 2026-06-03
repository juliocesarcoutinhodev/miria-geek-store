package br.com.miriageekstore.catalog.domain.port.in;

public interface UpdateProductUseCase {
    UpdateProductResult execute(UpdateProductCommand command);
}
