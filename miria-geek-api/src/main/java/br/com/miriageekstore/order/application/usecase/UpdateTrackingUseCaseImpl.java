package br.com.miriageekstore.order.application.usecase;

import br.com.miriageekstore.order.domain.event.OrderTrackingUpdated;
import br.com.miriageekstore.order.domain.exception.OrderNotFoundException;
import br.com.miriageekstore.order.domain.model.Carrier;
import br.com.miriageekstore.order.domain.model.OrderId;
import br.com.miriageekstore.order.domain.port.in.UpdateTrackingCommand;
import br.com.miriageekstore.order.domain.port.in.UpdateTrackingResult;
import br.com.miriageekstore.order.domain.port.in.UpdateTrackingUseCase;
import br.com.miriageekstore.order.domain.port.out.OrderEventPublisher;
import br.com.miriageekstore.order.domain.port.out.OrderWriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UpdateTrackingUseCaseImpl implements UpdateTrackingUseCase {

    private final OrderWriteRepository orderWriteRepository;
    private final OrderEventPublisher eventPublisher;

    @Override
    @Transactional
    public UpdateTrackingResult execute(UpdateTrackingCommand command) {
        if (command.carrier() == Carrier.OUTRO && isBlank(command.customTrackingUrl())) {
            throw new IllegalArgumentException(
                    "urlRastreamentoCustom é obrigatória quando transportadora é OUTRO");
        }

        var orderId = OrderId.of(command.orderId());
        var order   = orderWriteRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        order.requireShipped();

        var trackingUrl = command.carrier().buildTrackingUrl(
                command.trackingCode(), command.customTrackingUrl());

        orderWriteRepository.saveTracking(
                orderId, command.trackingCode(), command.carrier(),
                command.carrierName(), trackingUrl);

        eventPublisher.publish(new OrderTrackingUpdated(
                command.orderId(), order.getUserId(),
                order.getOrderNumber(), order.getCustomerEmail(), order.getCustomerName(),
                command.trackingCode(), command.carrier().name(),
                command.carrierName(), trackingUrl, Instant.now()));

        return new UpdateTrackingResult(
                command.orderId(), order.getOrderNumber(), order.getStatus().name(),
                command.trackingCode(), command.carrier().name(),
                command.carrierName(), trackingUrl);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
