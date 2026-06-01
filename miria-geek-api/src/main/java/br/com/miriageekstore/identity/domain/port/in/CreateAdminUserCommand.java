package br.com.miriageekstore.identity.domain.port.in;

public record CreateAdminUserCommand(String fullName, String email) {}
