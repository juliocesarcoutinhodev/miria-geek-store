package br.com.miriageekstore.identity.infrastructure.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

record AddressResponse(
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
