package br.com.miriageekstore.identity.domain.model;

import java.time.Instant;
import java.util.UUID;

public record RefreshToken(
        UUID id,
        UserId userId,
        String tokenHash,
        Instant expiresAt,
        String ipAddress,
        String userAgent,
        Instant createdAt,
        UUID familyId,
        boolean revoked,
        Instant revokedAt
) {
    public static RefreshToken create(UserId userId, String tokenHash,
                                      String ipAddress, String userAgent,
                                      long expirySeconds) {
        var id = UUID.randomUUID();
        return new RefreshToken(
                id, userId, tokenHash,
                Instant.now().plusSeconds(expirySeconds),
                ipAddress, userAgent, Instant.now(),
                id, false, null
        );
    }

    public static RefreshToken createRotated(UserId userId, String tokenHash,
                                              String ipAddress, String userAgent,
                                              long expirySeconds, UUID familyId) {
        return new RefreshToken(
                UUID.randomUUID(), userId, tokenHash,
                Instant.now().plusSeconds(expirySeconds),
                ipAddress, userAgent, Instant.now(),
                familyId, false, null
        );
    }

    public RefreshToken revoke() {
        return new RefreshToken(id, userId, tokenHash, expiresAt,
                ipAddress, userAgent, createdAt, familyId, true, Instant.now());
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
