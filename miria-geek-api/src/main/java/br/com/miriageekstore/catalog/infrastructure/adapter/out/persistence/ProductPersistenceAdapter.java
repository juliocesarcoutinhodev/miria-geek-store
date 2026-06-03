package br.com.miriageekstore.catalog.infrastructure.adapter.out.persistence;

import br.com.miriageekstore.catalog.domain.model.Product;
import br.com.miriageekstore.catalog.domain.model.ProductId;
import br.com.miriageekstore.catalog.domain.port.out.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class ProductPersistenceAdapter implements ProductRepository {

    private final ProductJpaRepository jpaRepository;
    private final ProductVariantJpaRepository variantJpaRepository;
    private final ProductEntityMapper mapper;

    @Override
    public boolean existsBySku(String sku) {
        return variantJpaRepository.existsBySku(sku);
    }

    @Override
    public Product save(Product product) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(product)));
    }

    @Override
    public Optional<Product> findById(ProductId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }
}
