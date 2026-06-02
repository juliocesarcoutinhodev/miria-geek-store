package br.com.miriageekstore.identity.domain.exception;

public class CurrentPasswordMismatchException extends RuntimeException {
    public CurrentPasswordMismatchException() {
        super("Senha atual incorreta");
    }
}
