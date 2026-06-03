package br.com.miriageekstore.catalog.domain.exception;

import java.util.List;

public class ProductCannotBeActivatedException extends RuntimeException {

    public ProductCannotBeActivatedException(List<String> reasons) {
        super("Produto não pode ser ativado: " + String.join("; ", reasons));
    }
}
