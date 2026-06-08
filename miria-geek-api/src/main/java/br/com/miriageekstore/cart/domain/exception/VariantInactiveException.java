package br.com.miriageekstore.cart.domain.exception;

public class VariantInactiveException extends RuntimeException {
    public VariantInactiveException() {
        super("Esta variante está inativa e não pode ser adicionada ao carrinho");
    }
}
