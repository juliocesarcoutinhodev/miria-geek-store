package br.com.miriageekstore.order.domain.port.in;

import java.time.Instant;

public record OrderSummaryQuery(Instant startDate, Instant endDate) {}
