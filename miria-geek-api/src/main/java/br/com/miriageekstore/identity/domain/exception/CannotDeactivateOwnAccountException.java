package br.com.miriageekstore.identity.domain.exception;

public class CannotDeactivateOwnAccountException extends RuntimeException {
    public CannotDeactivateOwnAccountException() {
        super("Administrador não pode desativar a própria conta");
    }
}
