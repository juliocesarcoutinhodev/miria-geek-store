package br.com.miriageekstore.order.domain.port.out;

import br.com.miriageekstore.order.domain.model.Order;
import br.com.miriageekstore.order.domain.model.OrderId;
import br.com.miriageekstore.order.domain.model.OrderStatus;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface OrderWriteRepository {
    Optional<Order> findById(OrderId id);
    void updateStatus(OrderId id, OrderStatus status, Instant updatedAt);
    void saveHistoryEntry(UUID id, OrderId orderId, OrderStatus status, String note, UUID adminId, Instant changedAt);
    void restoreStockForOrder(OrderId orderId);
}
