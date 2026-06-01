package br.com.miriageekstore.identity.domain.exception;

public class CannotDeactivateOwnAccountException extends RuntimeException {
    public CannotDeactivateOwnAccountException() {
        super("Admin cannot deactivate their own account");
    }
}
