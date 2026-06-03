package br.com.miriageekstore.catalog.domain.port.in;

public interface ListCategoriesUseCase {
    ListCategoriesResult execute(ListCategoriesQuery query);
}
