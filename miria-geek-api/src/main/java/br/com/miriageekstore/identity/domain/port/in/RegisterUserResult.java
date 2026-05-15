package br.com.miriageekstore.identity.domain.port.in;

import java.time.Instant;
import java.util.UUID;

public record RegisterUserResult(
        UUID id,
        String fullName,
        String email,
        String status,
        Instant createdAt
) {}
