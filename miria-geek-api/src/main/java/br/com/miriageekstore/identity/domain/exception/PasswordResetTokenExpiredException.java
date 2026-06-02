package br.com.miriageekstore.identity.domain.exception;

public class PasswordResetTokenExpiredException extends RuntimeException {
    public PasswordResetTokenExpiredException() {
        super("Token de redefinição de senha expirado. Solicite um novo.");
    }
}
