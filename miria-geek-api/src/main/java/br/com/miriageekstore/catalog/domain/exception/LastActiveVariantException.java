package br.com.miriageekstore.catalog.domain.exception;

public class LastActiveVariantException extends RuntimeException {

    public LastActiveVariantException() {
        super("Não é possível inativar a última variante ativa do produto");
    }
}
