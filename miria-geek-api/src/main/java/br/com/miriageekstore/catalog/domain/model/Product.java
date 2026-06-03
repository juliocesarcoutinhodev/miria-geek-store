package br.com.miriageekstore.catalog.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Product {

    private final ProductId id;
    private String name;
    private Slug slug;
    private String description;
    private final CategoryId categoryId;
    private ProductStatus status;
    private boolean featured;
    private final List<ProductVariant> variants;
    private final Instant createdAt;

    private Product(ProductId id, String name, Slug slug, String description,
                    CategoryId categoryId, ProductStatus status, boolean featured,
                    List<ProductVariant> variants, Instant createdAt) {
        if (variants == null || variants.isEmpty()) {
            throw new IllegalArgumentException("O produto deve ter ao menos uma variante");
        }
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.categoryId = categoryId;
        this.status = status;
        this.featured = featured;
        this.variants = new ArrayList<>(variants);
        this.createdAt = createdAt;
    }

    public static Product create(String name, String description, CategoryId categoryId,
                                  boolean featured, List<ProductVariant> variants) {
        return new Product(
                ProductId.generate(),
                name,
                Slug.from(name),
                description,
                categoryId,
                ProductStatus.INACTIVE,
                featured,
                variants,
                Instant.now()
        );
    }

    public static Product reconstitute(ProductId id, String name, Slug slug, String description,
                                        CategoryId categoryId, ProductStatus status, boolean featured,
                                        List<ProductVariant> variants, Instant createdAt) {
        return new Product(id, name, slug, description, categoryId, status, featured, variants, createdAt);
    }

    public ProductId getId() { return id; }
    public String getName() { return name; }
    public Slug getSlug() { return slug; }
    public String getDescription() { return description; }
    public CategoryId getCategoryId() { return categoryId; }
    public ProductStatus getStatus() { return status; }
    public boolean isFeatured() { return featured; }
    public List<ProductVariant> getVariants() { return Collections.unmodifiableList(variants); }
    public Instant getCreatedAt() { return createdAt; }
}
