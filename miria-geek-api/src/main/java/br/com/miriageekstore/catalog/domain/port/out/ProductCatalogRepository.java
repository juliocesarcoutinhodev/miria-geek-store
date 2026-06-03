package br.com.miriageekstore.catalog.domain.port.out;

import br.com.miriageekstore.catalog.domain.port.in.GetProductDetailResult;
import br.com.miriageekstore.catalog.domain.port.in.ListProductsResult;
import br.com.miriageekstore.catalog.domain.port.in.ProductSearchQuery;

import java.util.Optional;

public interface ProductCatalogRepository {
    ListProductsResult search(ProductSearchQuery query);
    Optional<GetProductDetailResult> findBySlug(String slug);
}
