package br.com.miriageekstore.notification.infrastructure.adapter.in.messaging;

import br.com.miriageekstore.notification.infrastructure.adapter.out.mail.OrderTrackingMailSender;
import br.com.miriageekstore.order.domain.event.OrderTrackingUpdated;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTrackingEventListener {

    private final OrderTrackingMailSender mailSender;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order.tracking-updated",
                   groupId = "${KAFKA_GROUP_ID:miria-geek-dev}-notification")
    public void handle(@Payload String payload) {
        try {
            var event = objectMapper.readValue(payload, OrderTrackingUpdated.class);
            log.debug("Received OrderTrackingUpdated for orderId={}", event.orderId());
            mailSender.sendTrackingEmail(event);
        } catch (Exception e) {
            log.error("Failed to process OrderTrackingUpdated event: {}", e.getMessage(), e);
        }
    }
}
