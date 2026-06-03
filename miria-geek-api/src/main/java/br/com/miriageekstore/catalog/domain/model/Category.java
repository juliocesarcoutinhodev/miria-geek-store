package br.com.miriageekstore.catalog.domain.model;

import java.time.Instant;

public class Category {

    private final CategoryId id;
    private String name;
    private Slug slug;
    private String description;
    private boolean active;
    private final Instant createdAt;

    private Category(CategoryId id, String name, Slug slug, String description,
                     boolean active, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.active = active;
        this.createdAt = createdAt;
    }

    public static Category create(String name, String description) {
        return new Category(
                CategoryId.generate(),
                name,
                Slug.from(name),
                description,
                true,
                Instant.now()
        );
    }

    public static Category reconstitute(CategoryId id, String name, Slug slug,
                                         String description, boolean active, Instant createdAt) {
        return new Category(id, name, slug, description, active, createdAt);
    }

    public void update(String name, String description, boolean active) {
        this.slug = Slug.from(name);
        this.name = name;
        this.description = description;
        this.active = active;
    }

    public CategoryId getId() { return id; }
    public String getName() { return name; }
    public Slug getSlug() { return slug; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
}
