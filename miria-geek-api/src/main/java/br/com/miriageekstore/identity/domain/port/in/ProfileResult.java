package br.com.miriageekstore.identity.domain.port.in;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record ProfileResult(UUID id, String fullName, String email, String status, Set<String> roles, Instant createdAt) {}
