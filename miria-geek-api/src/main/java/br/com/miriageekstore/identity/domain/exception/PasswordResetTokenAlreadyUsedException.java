package br.com.miriageekstore.identity.domain.exception;

public class PasswordResetTokenAlreadyUsedException extends RuntimeException {
    public PasswordResetTokenAlreadyUsedException() {
        super("Token de redefinição de senha já utilizado");
    }
}
