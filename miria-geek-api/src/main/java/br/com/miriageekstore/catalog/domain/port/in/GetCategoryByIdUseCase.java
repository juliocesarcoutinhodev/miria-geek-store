package br.com.miriageekstore.catalog.domain.port.in;

import br.com.miriageekstore.catalog.domain.model.CategoryId;

public interface GetCategoryByIdUseCase {
    GetCategoryByIdResult execute(CategoryId id);
}
