package br.com.miriageekstore.catalog.domain.port.in;

public interface UpdateVariantUseCase {
    UpdateVariantResult execute(UpdateVariantCommand command);
}
