package br.com.miriageekstore.cart.domain.exception;

public class VariantMissingDimensionsException extends RuntimeException {
    public VariantMissingDimensionsException() {
        super("Produto sem dimensões cadastradas, não é possível calcular o frete");
    }
}
