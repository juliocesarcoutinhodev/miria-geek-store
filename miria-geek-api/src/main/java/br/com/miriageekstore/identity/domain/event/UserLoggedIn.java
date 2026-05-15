package br.com.miriageekstore.identity.domain.event;

import java.time.Instant;
import java.util.UUID;

public record UserLoggedIn(UUID userId, String email, String fullName, Instant occurredAt) {}
