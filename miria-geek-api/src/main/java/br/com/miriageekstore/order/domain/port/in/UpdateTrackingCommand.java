package br.com.miriageekstore.order.domain.port.in;

import br.com.miriageekstore.order.domain.model.Carrier;

import java.util.UUID;

public record UpdateTrackingCommand(
        UUID orderId,
        String trackingCode,
        Carrier carrier,
        String carrierName,
        String customTrackingUrl
) {}
