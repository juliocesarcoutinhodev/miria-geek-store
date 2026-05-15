package br.com.miriageekstore.identity.domain.event;

import java.time.Instant;
import java.util.UUID;

public record UserEmailVerified(
        UUID userId,
        String email,
        Instant occurredAt
) {}
