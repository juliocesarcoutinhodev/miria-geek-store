package br.com.miriageekstore.identity.domain.exception;

public class VerificationTokenNotFoundException extends RuntimeException {
    public VerificationTokenNotFoundException() {
        super("Token de verificação não encontrado");
    }
}
