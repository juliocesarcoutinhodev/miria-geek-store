package br.com.miriageekstore.identity.domain.exception;

public class AddressLinkedToActiveOrderException extends RuntimeException {
    public AddressLinkedToActiveOrderException() {
        super("Endereço não pode ser removido pois está vinculado a um pedido ativo");
    }
}
