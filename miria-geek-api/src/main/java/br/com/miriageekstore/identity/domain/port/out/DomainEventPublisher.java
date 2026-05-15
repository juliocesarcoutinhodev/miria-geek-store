package br.com.miriageekstore.identity.domain.port.out;

public interface DomainEventPublisher {
    void publish(Object event);
}
