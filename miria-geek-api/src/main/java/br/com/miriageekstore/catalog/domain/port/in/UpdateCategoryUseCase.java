package br.com.miriageekstore.catalog.domain.port.in;

public interface UpdateCategoryUseCase {
    UpdateCategoryResult execute(UpdateCategoryCommand command);
}
