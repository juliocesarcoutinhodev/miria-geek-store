package br.com.miriageekstore.identity.domain.event;

import java.time.Instant;
import java.util.UUID;

public record PasswordResetRequested(UUID userId, String email, String fullName, UUID resetToken, Instant occurredAt) {}
