package br.com.miriageekstore.order.application.usecase;

import br.com.miriageekstore.order.domain.port.in.GetOrderSummaryUseCase;
import br.com.miriageekstore.order.domain.port.in.OrderSummaryQuery;
import br.com.miriageekstore.order.domain.port.in.OrderSummaryResult;
import br.com.miriageekstore.order.domain.port.out.AdminOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class GetOrderSummaryUseCaseImpl implements GetOrderSummaryUseCase {

    private final AdminOrderRepository repository;

    @Override
    @Transactional(readOnly = true)
    public OrderSummaryResult execute(OrderSummaryQuery query) {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);

        Instant start = query.startDate() != null
                ? query.startDate()
                : today.atStartOfDay(ZoneOffset.UTC).toInstant();

        Instant end = query.endDate() != null
                ? query.endDate()
                : today.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().minusNanos(1);

        return repository.getSummary(start, end);
    }
}
