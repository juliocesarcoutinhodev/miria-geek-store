package br.com.miriageekstore.identity.infrastructure.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

record RegisterResponse(
        UUID id,
        String fullName,
        String email,
        String status,
        Instant createdAt
) {}
