package br.com.miriageekstore.identity.domain.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public record VerificationToken(UUID token, Instant expiresAt) {

    public static VerificationToken generate() {
        return new VerificationToken(UUID.randomUUID(), Instant.now().plus(24, ChronoUnit.HOURS));
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
