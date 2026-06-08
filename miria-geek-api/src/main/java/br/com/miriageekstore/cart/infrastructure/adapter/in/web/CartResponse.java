package br.com.miriageekstore.cart.infrastructure.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

record CartResponse(
        UUID id,
        UUID userId,
        List<CartItemResponse> items,
        SelectedShippingResponse selectedShipping,
        BigDecimal subtotal,
        BigDecimal freight,
        BigDecimal total,
        int itemCount,
        Instant createdAt,
        Instant updatedAt
) {
    record SelectedShippingResponse(
            String id,
            String name,
            String carrier,
            BigDecimal value,
            int deliveryDays
    ) {}
}
