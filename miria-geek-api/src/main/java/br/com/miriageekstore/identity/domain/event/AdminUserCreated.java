package br.com.miriageekstore.identity.domain.event;

import java.time.Instant;
import java.util.UUID;

public record AdminUserCreated(UUID userId, String email, String fullName, UUID createdByAdminId, Instant occurredAt) {}
