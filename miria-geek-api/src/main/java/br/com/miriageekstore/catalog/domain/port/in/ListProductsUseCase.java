package br.com.miriageekstore.catalog.domain.port.in;

public interface ListProductsUseCase {
    ListProductsResult execute(ProductSearchQuery query);
}
