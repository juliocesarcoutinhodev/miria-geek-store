package br.com.miriageekstore.order.infrastructure.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderSummaryResponse(
        Period period,
        Totals totals,
        List<StatusSummary> byStatus
) {
    public record Period(Instant startDate, Instant endDate) {}

    public record Totals(long totalOrders, BigDecimal totalRevenue, BigDecimal averageTicket) {}

    public record StatusSummary(String status, long count, double percentage, BigDecimal totalValue) {}
}
