package br.com.miriageekstore.catalog.domain.port.in;

import br.com.miriageekstore.catalog.domain.model.CategoryId;

public interface DeleteCategoryUseCase {
    void execute(CategoryId id);
}
