package br.com.miriageekstore.identity.domain.port.in;

public record ChangePasswordResult(String accessToken, String refreshToken) {}
