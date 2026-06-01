package br.com.miriageekstore.identity.domain.exception;

public class AddressLinkedToActiveOrderException extends RuntimeException {
    public AddressLinkedToActiveOrderException() {
        super("Address cannot be removed because it is linked to an active order");
    }
}
