package br.com.miriageekstore.catalog.infrastructure.adapter.out.persistence;

import br.com.miriageekstore.catalog.domain.model.CategoryId;
import br.com.miriageekstore.catalog.domain.model.Product;
import br.com.miriageekstore.catalog.domain.model.ProductId;
import br.com.miriageekstore.catalog.domain.model.ProductVariant;
import br.com.miriageekstore.catalog.domain.model.Sku;
import br.com.miriageekstore.catalog.domain.model.Slug;
import br.com.miriageekstore.catalog.domain.model.VariantId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class ProductEntityMapper {

    ProductEntity toEntity(Product product) {
        var entity = new ProductEntity();
        entity.setId(product.getId().value());
        entity.setName(product.getName());
        entity.setSlug(product.getSlug().value());
        entity.setDescription(product.getDescription());
        entity.setCategoryId(product.getCategoryId().value());
        entity.setStatus(product.getStatus());
        entity.setFeatured(product.isFeatured());
        entity.setCreatedAt(product.getCreatedAt());

        List<ProductVariantEntity> variantEntities = product.getVariants().stream()
                .map(v -> toVariantEntity(v, entity))
                .toList();
        entity.getVariants().clear();
        entity.getVariants().addAll(variantEntities);

        return entity;
    }

    Product toDomain(ProductEntity entity) {
        var variants = entity.getVariants().stream()
                .map(v -> ProductVariant.reconstitute(
                        VariantId.of(v.getId()),
                        v.getAttributeName(),
                        v.getAttributeValue(),
                        v.getPrice(),
                        v.getStock(),
                        new Sku(v.getSku()),
                        v.isActive(),
                        v.getCreatedAt()
                ))
                .toList();

        return Product.reconstitute(
                ProductId.of(entity.getId()),
                entity.getName(),
                new Slug(entity.getSlug()),
                entity.getDescription(),
                CategoryId.of(entity.getCategoryId()),
                entity.getStatus(),
                entity.isFeatured(),
                variants,
                entity.getCreatedAt());
    }

    private ProductVariantEntity toVariantEntity(ProductVariant variant, ProductEntity productEntity) {
        var entity = new ProductVariantEntity();
        entity.setId(variant.getId().value());
        entity.setProduct(productEntity);
        entity.setAttributeName(variant.getAttributeName());
        entity.setAttributeValue(variant.getAttributeValue());
        entity.setPrice(variant.getPrice());
        entity.setStock(variant.getStock());
        entity.setSku(variant.getSku().value());
        entity.setActive(variant.isActive());
        entity.setCreatedAt(variant.getCreatedAt());
        return entity;
    }
}
