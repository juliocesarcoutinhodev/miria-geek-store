package br.com.miriageekstore.identity.domain.exception;

public class CannotChangeOwnRoleException extends RuntimeException {
    public CannotChangeOwnRoleException() {
        super("Administrador não pode alterar o próprio cargo");
    }
}
