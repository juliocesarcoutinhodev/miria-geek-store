package br.com.miriageekstore.cart.infrastructure.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

record CartResponse(
        UUID id,
        UUID userId,
        List<CartItemResponse> items,
        BigDecimal subtotal,
        int itemCount,
        Instant updatedAt
) {}
