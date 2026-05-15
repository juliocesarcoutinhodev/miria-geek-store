package br.com.miriageekstore.identity.infrastructure.adapter.out.messaging;

import br.com.miriageekstore.identity.domain.event.UserRegistered;
import br.com.miriageekstore.identity.domain.port.out.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class KafkaEventPublisher implements DomainEventPublisher {

    private static final String TOPIC_USER_REGISTERED = "identity.user-registered";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(Object event) {
        if (event instanceof UserRegistered e) {
            log.debug("Publishing UserRegistered event for userId={}", e.userId());
            kafkaTemplate.send(TOPIC_USER_REGISTERED, e.userId().toString(), e);
        } else {
            log.warn("Unknown event type: {}", event.getClass().getName());
        }
    }
}
