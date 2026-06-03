package br.com.miriageekstore.catalog.domain.exception;

public class CategoryHasProductsException extends RuntimeException {

    public CategoryHasProductsException() {
        super("Não é possível excluir uma categoria que possui produtos vinculados");
    }
}
