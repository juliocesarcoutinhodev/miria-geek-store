package br.com.miriageekstore.identity.infrastructure.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

record AdminUserResponse(
        UUID id,
        String fullName,
        String email,
        String status,
        String role,
        Instant createdAt,
        UUID createdByAdminId
) {}
