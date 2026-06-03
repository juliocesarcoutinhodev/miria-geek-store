package br.com.miriageekstore.catalog.domain.port.in;

public interface CreateCategoryUseCase {
    CreateCategoryResult execute(CreateCategoryCommand command);
}
