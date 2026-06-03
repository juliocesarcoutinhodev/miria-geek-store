package br.com.miriageekstore.catalog.domain.exception;

public class ProductSkuAlreadyExistsException extends RuntimeException {

    public ProductSkuAlreadyExistsException(String sku) {
        super("Já existe um produto com o SKU: " + sku);
    }
}
