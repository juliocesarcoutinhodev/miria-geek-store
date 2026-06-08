package br.com.miriageekstore.cart.domain.port.in;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CartResult(
        UUID id,
        UUID userId,
        List<CartItemResult> items,
        SelectedShipping selectedShipping,
        BigDecimal subtotal,
        BigDecimal freight,
        BigDecimal total,
        int itemCount,
        Instant createdAt,
        Instant updatedAt
) {
    public record SelectedShipping(
            String id,
            String name,
            String carrier,
            BigDecimal value,
            int deliveryDays
    ) {}
}
