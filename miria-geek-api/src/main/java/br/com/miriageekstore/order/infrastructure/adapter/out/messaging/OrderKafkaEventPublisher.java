package br.com.miriageekstore.order.infrastructure.adapter.out.messaging;

import br.com.miriageekstore.order.domain.event.OrderCancelled;
import br.com.miriageekstore.order.domain.event.OrderStatusChanged;
import br.com.miriageekstore.order.domain.port.out.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class OrderKafkaEventPublisher implements OrderEventPublisher {

    private static final String TOPIC_STATUS_CHANGED = "order.status-changed";
    private static final String TOPIC_CANCELLED      = "order.cancelled";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(Object event) {
        if (event instanceof OrderStatusChanged e) {
            log.debug("Publishing OrderStatusChanged for orderId={}", e.orderId());
            kafkaTemplate.send(TOPIC_STATUS_CHANGED, e.orderId().toString(), e);
        } else if (event instanceof OrderCancelled e) {
            log.debug("Publishing OrderCancelled for orderId={}", e.orderId());
            kafkaTemplate.send(TOPIC_CANCELLED, e.orderId().toString(), e);
        } else {
            log.warn("Unknown order event type: {}", event.getClass().getName());
        }
    }
}
