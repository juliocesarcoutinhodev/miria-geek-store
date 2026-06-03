package br.com.miriageekstore.catalog.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {

    long countByCategoryId(UUID categoryId);
}
