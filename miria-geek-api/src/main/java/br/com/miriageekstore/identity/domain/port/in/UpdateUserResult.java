package br.com.miriageekstore.identity.domain.port.in;

import java.time.Instant;
import java.util.UUID;

public record UpdateUserResult(
        UUID id,
        String fullName,
        String email,
        String role,
        String status,
        Instant createdAt
) {}
