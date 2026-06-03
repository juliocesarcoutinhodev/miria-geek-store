package br.com.miriageekstore.catalog.infrastructure.adapter.out.persistence;

import br.com.miriageekstore.catalog.domain.model.ProductStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
class ProductEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status;

    @Column(nullable = false)
    private boolean featured;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ProductVariantEntity> variants = new ArrayList<>();
}
