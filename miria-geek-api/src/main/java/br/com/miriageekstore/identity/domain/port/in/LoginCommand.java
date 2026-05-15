package br.com.miriageekstore.identity.domain.port.in;

public record LoginCommand(String email, String password, String ipAddress, String userAgent) {}
