package br.com.miriageekstore.catalog.domain.port.out;

import br.com.miriageekstore.catalog.domain.port.in.ListProductsResult;
import br.com.miriageekstore.catalog.domain.port.in.ProductSearchQuery;

public interface ProductCatalogRepository {
    ListProductsResult search(ProductSearchQuery query);
}
