package br.com.miriageekstore.catalog.domain.exception;

public class ProductImageNotFoundException extends RuntimeException {

    public ProductImageNotFoundException() {
        super("Imagem do produto não encontrada");
    }
}
