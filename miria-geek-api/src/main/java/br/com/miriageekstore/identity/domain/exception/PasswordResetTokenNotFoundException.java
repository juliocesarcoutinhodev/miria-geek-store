package br.com.miriageekstore.identity.domain.exception;

public class PasswordResetTokenNotFoundException extends RuntimeException {
    public PasswordResetTokenNotFoundException() {
        super("Token de redefinição de senha não encontrado");
    }
}
