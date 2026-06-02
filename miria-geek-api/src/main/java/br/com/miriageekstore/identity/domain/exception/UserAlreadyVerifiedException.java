package br.com.miriageekstore.identity.domain.exception;

public class UserAlreadyVerifiedException extends RuntimeException {
    public UserAlreadyVerifiedException() {
        super("E-mail já verificado");
    }
}
