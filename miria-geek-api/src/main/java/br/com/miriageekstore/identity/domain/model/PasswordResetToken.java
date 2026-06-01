package br.com.miriageekstore.identity.domain.model;

import java.time.Instant;
import java.util.UUID;

public record PasswordResetToken(
        UUID id,
        UserId userId,
        UUID token,
        Instant expiresAt,
        boolean used,
        Instant usedAt,
        Instant createdAt
) {
    public static PasswordResetToken create(UserId userId, long expirySeconds) {
        var now = Instant.now();
        return new PasswordResetToken(
                UUID.randomUUID(), userId, UUID.randomUUID(),
                now.plusSeconds(expirySeconds), false, null, now
        );
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public PasswordResetToken markAsUsed() {
        return new PasswordResetToken(id, userId, token, expiresAt, true, Instant.now(), createdAt);
    }
}
