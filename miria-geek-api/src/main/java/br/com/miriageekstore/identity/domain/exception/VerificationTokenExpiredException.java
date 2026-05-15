package br.com.miriageekstore.identity.domain.exception;

public class VerificationTokenExpiredException extends RuntimeException {
    public VerificationTokenExpiredException() {
        super("Verification token has expired. Please request a new verification email.");
    }
}
