package br.com.miriageekstore.order.domain.event;

import java.time.Instant;
import java.util.UUID;

public record OrderStatusChanged(
        UUID orderId,
        UUID userId,
        String previousStatus,
        String newStatus,
        String note,
        Instant occurredAt
) {}
