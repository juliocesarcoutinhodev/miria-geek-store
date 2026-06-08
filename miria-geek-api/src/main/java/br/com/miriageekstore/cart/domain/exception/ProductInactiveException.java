package br.com.miriageekstore.cart.domain.exception;

public class ProductInactiveException extends RuntimeException {
    public ProductInactiveException() {
        super("Este produto está inativo e não pode ser adicionado ao carrinho");
    }
}
