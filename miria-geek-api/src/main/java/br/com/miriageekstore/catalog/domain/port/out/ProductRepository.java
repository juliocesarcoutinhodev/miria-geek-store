package br.com.miriageekstore.catalog.domain.port.out;

import br.com.miriageekstore.catalog.domain.model.Product;
import br.com.miriageekstore.catalog.domain.model.ProductId;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    boolean existsBySku(String sku);

    boolean existsBySkuExcluding(String sku, UUID variantId);

    Product save(Product product);

    Optional<Product> findById(ProductId id);
}
