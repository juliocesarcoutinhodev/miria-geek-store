package br.com.miriageekstore.catalog.domain.port.in;

public interface UpdateVariantStatusUseCase {
    UpdateVariantStatusResult execute(UpdateVariantStatusCommand command);
}
