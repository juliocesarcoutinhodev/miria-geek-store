package br.com.miriageekstore.catalog.infrastructure.adapter.out.persistence;

import br.com.miriageekstore.catalog.domain.model.ImageId;
import br.com.miriageekstore.catalog.domain.model.ProductId;
import br.com.miriageekstore.catalog.domain.model.ProductImage;
import org.springframework.stereotype.Component;

@Component
class ProductImageEntityMapper {

    ProductImageEntity toEntity(ProductImage image) {
        var entity = new ProductImageEntity();
        entity.setId(image.getId().value());
        entity.setProductId(image.getProductId().value());
        entity.setUrl(image.getUrl());
        entity.setFilename(image.getFilename());
        entity.setPrincipal(image.isPrincipal());
        entity.setImageOrder(image.getImageOrder());
        entity.setCreatedAt(image.getCreatedAt());
        return entity;
    }

    ProductImage toDomain(ProductImageEntity entity) {
        return ProductImage.reconstitute(
                ImageId.of(entity.getId()),
                ProductId.of(entity.getProductId()),
                entity.getUrl(),
                entity.getFilename(),
                entity.isPrincipal(),
                entity.getImageOrder(),
                entity.getCreatedAt()
        );
    }
}
