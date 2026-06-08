package br.com.miriageekstore.cart.domain.exception;

public class CartVariantNotFoundException extends RuntimeException {
    public CartVariantNotFoundException() {
        super("Variante não encontrada");
    }
}
