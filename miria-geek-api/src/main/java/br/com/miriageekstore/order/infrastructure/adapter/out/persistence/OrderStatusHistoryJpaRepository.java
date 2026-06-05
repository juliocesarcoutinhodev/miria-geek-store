package br.com.miriageekstore.order.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface OrderStatusHistoryJpaRepository extends JpaRepository<OrderStatusHistoryEntity, UUID> {
    List<OrderStatusHistoryEntity> findAllByOrderIdOrderByChangedAtAsc(UUID orderId);
}
