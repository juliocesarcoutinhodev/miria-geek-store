package br.com.miriageekstore.order.domain.event;

import java.time.Instant;
import java.util.UUID;

public record OrderCancelled(
        UUID orderId,
        UUID userId,
        String previousStatus,
        String note,
        Instant occurredAt
) {}
