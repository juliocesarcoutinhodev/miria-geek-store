package br.com.miriageekstore.cart.domain.exception;

public class CartInsufficientStockException extends RuntimeException {
    public CartInsufficientStockException(int available) {
        super("Estoque insuficiente. Disponível: " + available);
    }
}
