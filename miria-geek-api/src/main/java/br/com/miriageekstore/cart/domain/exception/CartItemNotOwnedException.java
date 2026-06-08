package br.com.miriageekstore.cart.domain.exception;

public class CartItemNotOwnedException extends RuntimeException {
    public CartItemNotOwnedException() {
        super("Você não tem permissão para modificar este item");
    }
}
