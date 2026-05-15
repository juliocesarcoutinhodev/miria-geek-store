package br.com.miriageekstore.identity.domain.event;

import java.time.Instant;
import java.util.UUID;

public record UserRegistered(
        UUID userId,
        String email,
        String fullName,
        UUID verificationToken,
        Instant occurredAt
) {}
