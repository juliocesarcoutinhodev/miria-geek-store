package br.com.miriageekstore.identity.infrastructure.adapter.in.web;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

record ProfileResponse(UUID id, String name, String email, String status, Set<String> roles, Instant createdAt) {}
