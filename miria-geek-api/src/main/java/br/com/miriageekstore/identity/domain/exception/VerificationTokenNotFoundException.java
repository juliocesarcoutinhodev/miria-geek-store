package br.com.miriageekstore.identity.domain.exception;

public class VerificationTokenNotFoundException extends RuntimeException {
    public VerificationTokenNotFoundException() {
        super("Verification token not found");
    }
}
