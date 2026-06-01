package br.com.miriageekstore.identity.domain.exception;

public class CannotChangeOwnRoleException extends RuntimeException {
    public CannotChangeOwnRoleException() {
        super("Admin cannot change their own role");
    }
}
