package br.com.miriageekstore.identity.domain.exception;

public class AddressLimitExceededException extends RuntimeException {
    public AddressLimitExceededException() {
        super("Limite de 5 endereços por usuário atingido");
    }
}
