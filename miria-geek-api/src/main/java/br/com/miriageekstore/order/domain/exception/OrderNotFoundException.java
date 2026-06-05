package br.com.miriageekstore.order.domain.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException() {
        super("Pedido não encontrado");
    }
}
