package br.com.miriageekstore.identity.domain.port.in;

import java.time.Instant;
import java.util.UUID;

public record AddressResult(
        UUID id,
        String alias,
        String zipCode,
        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        String state,
        boolean isDefault,
        Instant createdAt
) {}
