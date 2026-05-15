package br.com.miriageekstore.identity.domain.exception;

public class InvalidPasswordPolicyException extends RuntimeException {
    public InvalidPasswordPolicyException(String message) {
        super(message);
    }
}
