package br.com.miriageekstore.catalog.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface ProductImageJpaRepository extends JpaRepository<ProductImageEntity, UUID> {

    List<ProductImageEntity> findAllByProductIdOrderByImageOrderAsc(UUID productId);

    long countByProductId(UUID productId);

    Optional<ProductImageEntity> findByProductIdAndPrincipalTrue(UUID productId);
}
