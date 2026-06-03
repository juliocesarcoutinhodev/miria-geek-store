package br.com.miriageekstore.catalog.domain.port.in;

public interface ListAdminProductsUseCase {
    ListAdminProductsResult execute(AdminProductQuery query);
}
