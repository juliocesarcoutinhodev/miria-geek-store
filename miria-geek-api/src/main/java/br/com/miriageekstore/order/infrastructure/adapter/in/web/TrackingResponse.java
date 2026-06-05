package br.com.miriageekstore.order.infrastructure.adapter.in.web;

import java.util.UUID;

record TrackingResponse(
        UUID id,
        String orderNumber,
        String status,
        String trackingCode,
        String carrier,
        String carrierName,
        String trackingUrl
) {}
