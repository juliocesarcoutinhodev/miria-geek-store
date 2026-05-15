package br.com.miriageekstore.identity.domain.port.in;

import java.util.Set;

public record LoginResult(String accessToken, String refreshToken, String email, Set<String> roles) {}
