package br.com.miriageekstore.identity.domain.exception;

public class PasswordConfirmationException extends RuntimeException {
    public PasswordConfirmationException() {
        super("Senha e confirmação não coincidem");
    }
}
