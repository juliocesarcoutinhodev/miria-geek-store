package br.com.miriageekstore.order.domain.exception;

import br.com.miriageekstore.order.domain.model.OrderStatus;

public class OrderNotShippedException extends RuntimeException {

    public OrderNotShippedException(OrderStatus currentStatus) {
        super("Código de rastreamento só pode ser informado para pedidos em SHIPPED. Status atual: " + currentStatus);
    }
}
