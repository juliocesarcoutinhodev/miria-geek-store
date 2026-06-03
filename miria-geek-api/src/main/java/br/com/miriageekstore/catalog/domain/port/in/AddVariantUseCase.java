package br.com.miriageekstore.catalog.domain.port.in;

public interface AddVariantUseCase {
    AddVariantResult execute(AddVariantCommand command);
}
