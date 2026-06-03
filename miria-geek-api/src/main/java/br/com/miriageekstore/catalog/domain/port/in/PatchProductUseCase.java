package br.com.miriageekstore.catalog.domain.port.in;

public interface PatchProductUseCase {
    UpdateProductResult execute(PatchProductCommand command);
}
