package br.com.miriageekstore.order.domain.exception;

import br.com.miriageekstore.order.domain.model.OrderStatus;

public class InvalidOrderStatusTransitionException extends RuntimeException {

    public InvalidOrderStatusTransitionException(OrderStatus from, OrderStatus to) {
        super("Transição de " + from + " para " + to + " não é permitida");
    }
}
