package br.com.miriageekstore.identity.domain.exception;

public class NewPasswordSameAsCurrentException extends RuntimeException {
    public NewPasswordSameAsCurrentException() {
        super("A nova senha deve ser diferente da senha atual");
    }
}
