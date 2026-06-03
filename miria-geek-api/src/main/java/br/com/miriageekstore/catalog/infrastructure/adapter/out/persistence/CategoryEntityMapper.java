package br.com.miriageekstore.catalog.infrastructure.adapter.out.persistence;

import br.com.miriageekstore.catalog.domain.model.Category;
import br.com.miriageekstore.catalog.domain.model.CategoryId;
import br.com.miriageekstore.catalog.domain.model.Slug;
import org.springframework.stereotype.Component;

@Component
class CategoryEntityMapper {

    CategoryEntity toEntity(Category category) {
        var entity = new CategoryEntity();
        entity.setId(category.getId().value());
        entity.setName(category.getName());
        entity.setSlug(category.getSlug().value());
        entity.setDescription(category.getDescription());
        entity.setActive(category.isActive());
        entity.setCreatedAt(category.getCreatedAt());
        return entity;
    }

    Category toDomain(CategoryEntity entity) {
        return Category.reconstitute(
                CategoryId.of(entity.getId()),
                entity.getName(),
                new Slug(entity.getSlug()),
                entity.getDescription(),
                entity.isActive(),
                entity.getCreatedAt()
        );
    }
}
