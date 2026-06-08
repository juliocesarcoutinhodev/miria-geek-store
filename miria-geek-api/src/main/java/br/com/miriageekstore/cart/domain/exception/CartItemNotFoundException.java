package br.com.miriageekstore.cart.domain.exception;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException() {
        super("Item não encontrado no carrinho");
    }
}
