package br.com.miriageekstore.order.application.usecase;

import br.com.miriageekstore.order.domain.port.in.OrderSummaryQuery;
import br.com.miriageekstore.order.domain.port.in.OrderSummaryResult;
import br.com.miriageekstore.order.domain.port.out.AdminOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetOrderSummaryUseCaseTest {

    @Mock AdminOrderRepository repository;
    @InjectMocks GetOrderSummaryUseCaseImpl useCase;

    @Test
    void shouldApplyStartOfDayDefaultWhenStartDateIsNull() {
        when(repository.getSummary(any(), any())).thenReturn(emptySummary(null, null));

        useCase.execute(new OrderSummaryQuery(null, null));

        var captor = ArgumentCaptor.forClass(Instant.class);
        verify(repository).getSummary(captor.capture(), any());

        Instant expectedStart = LocalDate.now(ZoneOffset.UTC).atStartOfDay(ZoneOffset.UTC).toInstant();
        assertThat(captor.getValue()).isEqualTo(expectedStart);
    }

    @Test
    void shouldApplyEndOfDayDefaultWhenEndDateIsNull() {
        when(repository.getSummary(any(), any())).thenReturn(emptySummary(null, null));

        useCase.execute(new OrderSummaryQuery(null, null));

        var captor = ArgumentCaptor.forClass(Instant.class);
        verify(repository).getSummary(any(), captor.capture());

        Instant expectedEnd = LocalDate.now(ZoneOffset.UTC).plusDays(1)
                .atStartOfDay(ZoneOffset.UTC).toInstant().minusNanos(1);
        assertThat(captor.getValue()).isEqualTo(expectedEnd);
    }

    @Test
    void shouldUseProvidedDatesWhenBothPresent() {
        Instant start = Instant.parse("2026-06-01T00:00:00Z");
        Instant end   = Instant.parse("2026-06-30T23:59:59Z");
        when(repository.getSummary(start, end)).thenReturn(emptySummary(start, end));

        useCase.execute(new OrderSummaryQuery(start, end));

        verify(repository).getSummary(start, end);
    }

    @Test
    void shouldReturnSummaryWithTotalsAndByStatus() {
        Instant start = Instant.parse("2026-06-01T00:00:00Z");
        Instant end   = Instant.parse("2026-06-30T23:59:59Z");

        var summary = new OrderSummaryResult(
                new OrderSummaryResult.Period(start, end),
                new OrderSummaryResult.Totals(10L, new BigDecimal("1500.00"), new BigDecimal("150.00")),
                List.of(
                        new OrderSummaryResult.StatusSummary("PAID", 6L, 60.0, new BigDecimal("900.00")),
                        new OrderSummaryResult.StatusSummary("CANCELLED", 4L, 40.0, new BigDecimal("600.00"))
                )
        );
        when(repository.getSummary(start, end)).thenReturn(summary);

        var result = useCase.execute(new OrderSummaryQuery(start, end));

        assertThat(result.totals().totalOrders()).isEqualTo(10L);
        assertThat(result.totals().totalRevenue()).isEqualByComparingTo("1500.00");
        assertThat(result.totals().averageTicket()).isEqualByComparingTo("150.00");
        assertThat(result.byStatus()).hasSize(2);
    }

    @Test
    void shouldReturnEmptySummaryWhenNoOrdersInPeriod() {
        Instant start = Instant.parse("2020-01-01T00:00:00Z");
        Instant end   = Instant.parse("2020-01-31T23:59:59Z");
        when(repository.getSummary(start, end)).thenReturn(emptySummary(start, end));

        var result = useCase.execute(new OrderSummaryQuery(start, end));

        assertThat(result.totals().totalOrders()).isZero();
        assertThat(result.totals().totalRevenue()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.byStatus()).isEmpty();
    }

    @Test
    void shouldReturnPeriodMatchingRequestedDates() {
        Instant start = Instant.parse("2026-06-01T00:00:00Z");
        Instant end   = Instant.parse("2026-06-30T23:59:59Z");
        when(repository.getSummary(start, end)).thenReturn(emptySummary(start, end));

        var result = useCase.execute(new OrderSummaryQuery(start, end));

        assertThat(result.period().startDate()).isEqualTo(start);
        assertThat(result.period().endDate()).isEqualTo(end);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private OrderSummaryResult emptySummary(Instant start, Instant end) {
        Instant s = start != null ? start : Instant.now();
        Instant e = end   != null ? end   : Instant.now();
        return new OrderSummaryResult(
                new OrderSummaryResult.Period(s, e),
                new OrderSummaryResult.Totals(0L, BigDecimal.ZERO, BigDecimal.ZERO),
                List.of()
        );
    }
}
