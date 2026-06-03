package br.com.miriageekstore.catalog.infrastructure.adapter.out.persistence;

import br.com.miriageekstore.catalog.domain.model.ImageId;
import br.com.miriageekstore.catalog.domain.model.ProductId;
import br.com.miriageekstore.catalog.domain.model.ProductImage;
import br.com.miriageekstore.catalog.domain.port.out.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
class ProductImagePersistenceAdapter implements ProductImageRepository {

    private final ProductImageJpaRepository jpaRepository;
    private final ProductImageEntityMapper mapper;

    @Override
    public ProductImage save(ProductImage image) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(image)));
    }

    @Override
    public Optional<ProductImage> findById(ImageId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<ProductImage> findAllByProductId(ProductId productId) {
        return jpaRepository.findAllByProductIdOrderByImageOrderAsc(productId.value())
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countByProductId(ProductId productId) {
        return jpaRepository.countByProductId(productId.value());
    }

    @Override
    public void deleteById(ImageId id) {
        jpaRepository.deleteById(id.value());
    }

    @Override
    public Optional<ProductImage> findPrincipalByProductId(ProductId productId) {
        return jpaRepository.findByProductIdAndPrincipalTrue(productId.value())
                .map(mapper::toDomain);
    }
}
