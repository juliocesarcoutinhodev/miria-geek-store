package br.com.miriageekstore.catalog.domain.exception;

public class VariantNotFoundException extends RuntimeException {

    public VariantNotFoundException() {
        super("Variante não encontrada");
    }
}
