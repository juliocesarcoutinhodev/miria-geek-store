package br.com.miriageekstore.order.domain.event;

import java.time.Instant;
import java.util.UUID;

public record OrderTrackingUpdated(
        UUID orderId,
        UUID userId,
        String orderNumber,
        String customerEmail,
        String customerName,
        String trackingCode,
        String carrier,
        String carrierName,
        String trackingUrl,
        Instant occurredAt
) {}
