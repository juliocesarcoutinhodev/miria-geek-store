package br.com.miriageekstore.order.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
}
