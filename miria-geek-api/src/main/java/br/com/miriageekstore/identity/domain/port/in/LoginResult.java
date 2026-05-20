package br.com.miriageekstore.identity.domain.port.in;

import java.util.Set;
import java.util.UUID;

public record LoginResult(String accessToken, String refreshToken, UUID id, String name, String email, Set<String> roles, String status) {}
