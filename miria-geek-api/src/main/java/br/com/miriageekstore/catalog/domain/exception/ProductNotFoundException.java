package br.com.miriageekstore.catalog.domain.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException() {
        super("Produto não encontrado");
    }
}
