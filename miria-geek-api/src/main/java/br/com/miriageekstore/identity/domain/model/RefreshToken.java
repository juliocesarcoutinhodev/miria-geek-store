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
        Instant createdAt
) {
    public static RefreshToken create(UserId userId, String tokenHash,
                                      String ipAddress, String userAgent,
                                      long expirySeconds) {
        return new RefreshToken(
                UUID.randomUUID(),
                userId,
                tokenHash,
                Instant.now().plusSeconds(expirySeconds),
                ipAddress,
                userAgent,
                Instant.now()
        );
    }
}
