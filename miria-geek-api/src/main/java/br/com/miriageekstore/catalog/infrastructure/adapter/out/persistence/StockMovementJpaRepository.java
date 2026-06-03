package br.com.miriageekstore.catalog.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface StockMovementJpaRepository extends JpaRepository<StockMovementEntity, UUID> {
}
