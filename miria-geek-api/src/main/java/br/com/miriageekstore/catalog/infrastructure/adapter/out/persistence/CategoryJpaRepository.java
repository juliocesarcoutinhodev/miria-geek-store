package br.com.miriageekstore.catalog.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

interface CategoryJpaRepository
        extends JpaRepository<CategoryEntity, UUID>, JpaSpecificationExecutor<CategoryEntity> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);

    List<CategoryEntity> findAllByActiveTrueOrderByNameAsc();
}
