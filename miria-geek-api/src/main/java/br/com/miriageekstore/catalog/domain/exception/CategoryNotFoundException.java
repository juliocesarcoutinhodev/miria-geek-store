package br.com.miriageekstore.catalog.domain.exception;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException() {
        super("Categoria não encontrada");
    }
}
