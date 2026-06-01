package br.com.miriageekstore.identity.domain.port.in;

public record AddressCommand(
        String alias,
        String zipCode,
        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        String state,
        boolean isDefault
) {}
