package br.com.miriageekstore.identity.domain.exception;

public class VerificationTokenExpiredException extends RuntimeException {
    public VerificationTokenExpiredException() {
        super("Token de verificação expirado. Solicite um novo e-mail de verificação.");
    }
}
