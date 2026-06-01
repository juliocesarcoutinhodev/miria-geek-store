package br.com.miriageekstore.identity.domain.exception;

public class PasswordResetTokenExpiredException extends RuntimeException {
    public PasswordResetTokenExpiredException() {
        super("Password reset token has expired. Please request a new one.");
    }
}
