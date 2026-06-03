package br.com.miriageekstore.catalog.domain.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException() {
        super("Estoque insuficiente para realizar a saída solicitada");
    }
}
