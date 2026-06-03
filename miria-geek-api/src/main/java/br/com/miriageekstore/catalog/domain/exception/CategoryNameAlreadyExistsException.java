package br.com.miriageekstore.catalog.domain.exception;

public class CategoryNameAlreadyExistsException extends RuntimeException {

    public CategoryNameAlreadyExistsException(String name) {
        super("Já existe uma categoria com o nome: " + name);
    }
}
