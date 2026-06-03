package br.com.miriageekstore.catalog.domain.port.out;

public interface CatalogEventPublisher {
    void publish(Object event);
}
