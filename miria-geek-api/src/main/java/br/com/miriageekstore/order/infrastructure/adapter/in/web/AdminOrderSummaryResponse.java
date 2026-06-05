package br.com.miriageekstore.order.infrastructure.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

record AdminOrderSummaryResponse(
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
