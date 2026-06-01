package br.com.miriageekstore.identity.domain.port.in;

import java.time.Instant;
import java.util.UUID;

public record CreateAdminUserResult(
        UUID id,
        String fullName,
        String email,
        String status,
        String role,
        Instant createdAt,
        UUID createdByAdminId
) {}
