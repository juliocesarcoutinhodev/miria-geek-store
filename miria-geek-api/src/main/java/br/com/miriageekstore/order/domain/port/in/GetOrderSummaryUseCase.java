package br.com.miriageekstore.order.domain.port.in;

public interface GetOrderSummaryUseCase {
    OrderSummaryResult execute(OrderSummaryQuery query);
}
