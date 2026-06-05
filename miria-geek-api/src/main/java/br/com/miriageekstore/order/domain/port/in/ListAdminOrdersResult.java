package br.com.miriageekstore.order.domain.port.in;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ListAdminOrdersResult(
        List<AdminOrderItem> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public record AdminOrderItem(
            UUID id,
            String orderNumber,
            String status,
            String customerName,
            String customerEmail,
            int totalItems,
            BigDecimal total,
            Instant createdAt,
            Instant updatedAt
    ) {}
}
