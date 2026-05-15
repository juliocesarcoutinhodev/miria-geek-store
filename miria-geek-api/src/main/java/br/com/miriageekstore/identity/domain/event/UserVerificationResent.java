package br.com.miriageekstore.identity.domain.event;

import java.time.Instant;
import java.util.UUID;

public record UserVerificationResent(
        UUID userId,
        String email,
        String fullName,
        UUID verificationToken,
        Instant occurredAt
) {}
