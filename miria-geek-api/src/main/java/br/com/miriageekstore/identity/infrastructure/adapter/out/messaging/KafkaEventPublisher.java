package br.com.miriageekstore.identity.infrastructure.adapter.out.messaging;

import br.com.miriageekstore.identity.domain.event.UserEmailVerified;
import br.com.miriageekstore.identity.domain.event.UserLoggedIn;
import br.com.miriageekstore.identity.domain.event.UserRegistered;
import br.com.miriageekstore.identity.domain.event.UserVerificationResent;
import br.com.miriageekstore.identity.domain.port.out.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class KafkaEventPublisher implements DomainEventPublisher {

    private static final String TOPIC_USER_REGISTERED       = "identity.user-registered";
    private static final String TOPIC_USER_EMAIL_VERIFIED   = "identity.user-email-verified";
    private static final String TOPIC_USER_LOGGED_IN        = "identity.user-logged-in";
    private static final String TOPIC_VERIFICATION_RESENT   = "identity.verification-resent";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(Object event) {
        if (event instanceof UserRegistered e) {
            log.debug("Publishing UserRegistered for userId={}", e.userId());
            kafkaTemplate.send(TOPIC_USER_REGISTERED, e.userId().toString(), e);
        } else if (event instanceof UserLoggedIn e) {
            log.debug("Publishing UserLoggedIn for userId={}", e.userId());
            kafkaTemplate.send(TOPIC_USER_LOGGED_IN, e.userId().toString(), e);
        } else if (event instanceof UserEmailVerified e) {
            log.debug("Publishing UserEmailVerified for userId={}", e.userId());
            kafkaTemplate.send(TOPIC_USER_EMAIL_VERIFIED, e.userId().toString(), e);
        } else if (event instanceof UserVerificationResent e) {
            log.debug("Publishing UserVerificationResent for userId={}", e.userId());
            kafkaTemplate.send(TOPIC_VERIFICATION_RESENT, e.userId().toString(), e);
        } else {
            log.warn("Unknown event type: {}", event.getClass().getName());
        }
    }
}
