package br.com.miriageekstore.identity.domain.exception;

public class PasswordResetTokenNotFoundException extends RuntimeException {
    public PasswordResetTokenNotFoundException() {
        super("Password reset token not found");
    }
}
