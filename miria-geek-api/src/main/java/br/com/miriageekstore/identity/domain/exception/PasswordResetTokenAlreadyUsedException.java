package br.com.miriageekstore.identity.domain.exception;

public class PasswordResetTokenAlreadyUsedException extends RuntimeException {
    public PasswordResetTokenAlreadyUsedException() {
        super("Password reset token has already been used");
    }
}
