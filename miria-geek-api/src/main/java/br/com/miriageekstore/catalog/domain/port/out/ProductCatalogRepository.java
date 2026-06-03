package br.com.miriageekstore.catalog.domain.port.out;

import br.com.miriageekstore.catalog.domain.port.in.AdminProductQuery;
import br.com.miriageekstore.catalog.domain.port.in.GetAdminProductDetailResult;
import br.com.miriageekstore.catalog.domain.port.in.GetProductDetailResult;
import br.com.miriageekstore.catalog.domain.port.in.ListAdminProductsResult;
import br.com.miriageekstore.catalog.domain.port.in.ListProductsResult;
import br.com.miriageekstore.catalog.domain.port.in.ProductSearchQuery;

import java.util.Optional;
import java.util.UUID;

public interface ProductCatalogRepository {
    // public (loja)
    ListProductsResult search(ProductSearchQuery query);
    Optional<GetProductDetailResult> findBySlug(String slug);

    // admin
    ListAdminProductsResult searchForAdmin(AdminProductQuery query);
    Optional<GetAdminProductDetailResult> findByIdForAdmin(UUID id);
}
