package br.com.miriageekstore.catalog.infrastructure.adapter.out.messaging;

import br.com.miriageekstore.catalog.domain.event.ProductStatusChanged;
import br.com.miriageekstore.catalog.domain.event.ProductUpdated;
import br.com.miriageekstore.catalog.domain.port.out.CatalogEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class CatalogKafkaEventPublisher implements CatalogEventPublisher {

    private static final String TOPIC_PRODUCT_UPDATED        = "catalog.product-updated";
    private static final String TOPIC_PRODUCT_STATUS_CHANGED = "catalog.product-status-changed";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(Object event) {
        if (event instanceof ProductUpdated e) {
            log.debug("Publishing ProductUpdated for productId={}", e.productId());
            kafkaTemplate.send(TOPIC_PRODUCT_UPDATED, e.productId().toString(), e);
        } else if (event instanceof ProductStatusChanged e) {
            log.debug("Publishing ProductStatusChanged for productId={}", e.productId());
            kafkaTemplate.send(TOPIC_PRODUCT_STATUS_CHANGED, e.productId().toString(), e);
        } else {
            log.warn("Unknown catalog event type: {}", event.getClass().getName());
        }
    }
}
