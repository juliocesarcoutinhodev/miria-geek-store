package br.com.miriageekstore.order.domain.port.in;

import java.util.UUID;

public record UpdateOrderStatusResult(
        UUID orderId,
        String previousStatus,
        String newStatus
) {}
