package br.com.miriageekstore.order.domain.port.in;

import java.util.UUID;

public record UpdateTrackingResult(
        UUID id,
        String orderNumber,
        String status,
        String trackingCode,
        String carrier,
        String carrierName,
        String trackingUrl
) {}
