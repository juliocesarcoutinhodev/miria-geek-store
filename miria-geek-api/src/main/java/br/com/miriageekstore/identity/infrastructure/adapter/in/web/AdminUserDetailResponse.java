package br.com.miriageekstore.identity.infrastructure.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

record AdminUserDetailResponse(
        UUID id,
        String fullName,
        String email,
        String role,
        String status,
        Instant createdAt,
        int totalPedidos,
        Instant ultimoLogin
) {}
