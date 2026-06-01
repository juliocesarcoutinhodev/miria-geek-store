package br.com.miriageekstore.identity.domain.exception;

public class NewPasswordSameAsCurrentException extends RuntimeException {
    public NewPasswordSameAsCurrentException() {
        super("New password must be different from the current password");
    }
}
