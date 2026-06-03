package br.com.miriageekstore.catalog.domain.port.out;

import br.com.miriageekstore.catalog.domain.model.Product;
import br.com.miriageekstore.catalog.domain.model.ProductId;

import java.util.Optional;

public interface ProductRepository {

    boolean existsBySku(String sku);

    Product save(Product product);

    Optional<Product> findById(ProductId id);
}
