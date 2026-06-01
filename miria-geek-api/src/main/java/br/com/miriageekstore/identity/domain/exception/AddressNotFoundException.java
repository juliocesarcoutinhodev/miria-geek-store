package br.com.miriageekstore.identity.domain.exception;

public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException() {
        super("Address not found");
    }
}
