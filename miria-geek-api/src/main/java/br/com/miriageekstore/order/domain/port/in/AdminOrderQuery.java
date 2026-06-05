package br.com.miriageekstore.order.domain.port.in;

import java.math.BigDecimal;
import java.time.Instant;

public record AdminOrderQuery(
        String orderNumber,
        String customerName,
        String customerEmail,
        String status,
        Instant startDate,
        Instant endDate,
        BigDecimal minValue,
        BigDecimal maxValue,
        int page,
        int size,
        String sort
) {}
