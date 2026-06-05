package br.com.miriageekstore.order.domain.port.in;

import br.com.miriageekstore.order.domain.model.OrderStatus;

import java.util.UUID;

public record UpdateOrderStatusCommand(
        UUID orderId,
        OrderStatus newStatus,
        String note,
        UUID adminId
) {}
