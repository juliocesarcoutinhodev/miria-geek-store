package br.com.miriageekstore.order.domain.port.out;

public interface OrderEventPublisher {
    void publish(Object event);
}
