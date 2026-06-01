package br.com.miriageekstore.identity.domain.exception;

public class AddressLimitExceededException extends RuntimeException {
    public AddressLimitExceededException() {
        super("Maximum of 5 addresses per user has been reached");
    }
}
