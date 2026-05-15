package br.com.miriageekstore.identity.domain.exception;

public class PasswordConfirmationException extends RuntimeException {
    public PasswordConfirmationException() {
        super("Password and confirmation do not match");
    }
}
