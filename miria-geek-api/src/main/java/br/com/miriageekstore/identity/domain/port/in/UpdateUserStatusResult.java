package br.com.miriageekstore.identity.domain.port.in;

import java.util.UUID;

public record UpdateUserStatusResult(UUID id, String fullName, String email, String status) {}
