package br.com.miriageekstore.order.domain.port.in;

public interface UpdateOrderStatusUseCase {
    UpdateOrderStatusResult execute(UpdateOrderStatusCommand command);
}
