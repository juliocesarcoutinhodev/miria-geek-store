package br.com.miriageekstore.order.application.usecase;

import br.com.miriageekstore.order.domain.event.OrderCancelled;
import br.com.miriageekstore.order.domain.event.OrderStatusChanged;
import br.com.miriageekstore.order.domain.exception.OrderNotFoundException;
import br.com.miriageekstore.order.domain.model.OrderId;
import br.com.miriageekstore.order.domain.model.OrderStatus;
import br.com.miriageekstore.order.domain.port.in.UpdateOrderStatusCommand;
import br.com.miriageekstore.order.domain.port.in.UpdateOrderStatusResult;
import br.com.miriageekstore.order.domain.port.in.UpdateOrderStatusUseCase;
import br.com.miriageekstore.order.domain.port.out.OrderEventPublisher;
import br.com.miriageekstore.order.domain.port.out.OrderWriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateOrderStatusUseCaseImpl implements UpdateOrderStatusUseCase {

    private final OrderWriteRepository orderWriteRepository;
    private final OrderEventPublisher eventPublisher;

    @Override
    @Transactional
    public UpdateOrderStatusResult execute(UpdateOrderStatusCommand command) {
        var orderId = OrderId.of(command.orderId());

        var order = orderWriteRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        var previousStatus = order.getStatus();

        order.transitionTo(command.newStatus());

        var now = Instant.now();

        orderWriteRepository.updateStatus(orderId, command.newStatus(), now);

        orderWriteRepository.saveHistoryEntry(
                UUID.randomUUID(), orderId, command.newStatus(),
                command.note(), command.adminId(), now);

        eventPublisher.publish(new OrderStatusChanged(
                command.orderId(), order.getUserId(),
                previousStatus.name(), command.newStatus().name(),
                command.note(), now));

        if (command.newStatus() == OrderStatus.CANCELLED) {
            orderWriteRepository.restoreStockForOrder(orderId);
            eventPublisher.publish(new OrderCancelled(
                    command.orderId(), order.getUserId(),
                    previousStatus.name(), command.note(), now));
        }

        return new UpdateOrderStatusResult(
                command.orderId(), previousStatus.name(), command.newStatus().name());
    }
}
