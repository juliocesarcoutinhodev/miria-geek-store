package br.com.miriageekstore.catalog.domain.port.in;

public interface UpdateProductStatusUseCase {
    UpdateProductStatusResult execute(UpdateProductStatusCommand command);
}
