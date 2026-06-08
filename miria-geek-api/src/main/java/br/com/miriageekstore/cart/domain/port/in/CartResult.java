package br.com.miriageekstore.cart.domain.port.in;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CartResult(
        UUID id,
        UUID userId,
        List<CartItemResult> items,
        BigDecimal subtotal,
        int itemCount,
        Instant updatedAt
) {}
