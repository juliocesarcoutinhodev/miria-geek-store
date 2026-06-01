package br.com.miriageekstore.identity.domain.port.in;

import java.time.Instant;
import java.util.UUID;

public record GetUserByIdResult(
        UUID id,
        String fullName,
        String email,
        String role,
        String status,
        Instant createdAt,
        int totalPedidos,
        Instant ultimoLogin
) {}
