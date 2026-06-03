package br.com.miriageekstore.catalog.domain.port.out;

import br.com.miriageekstore.catalog.domain.model.ImageId;
import br.com.miriageekstore.catalog.domain.model.ProductId;
import br.com.miriageekstore.catalog.domain.model.ProductImage;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository {

    ProductImage save(ProductImage image);

    Optional<ProductImage> findById(ImageId id);

    List<ProductImage> findAllByProductId(ProductId productId);

    long countByProductId(ProductId productId);

    void deleteById(ImageId id);

    Optional<ProductImage> findPrincipalByProductId(ProductId productId);
}
