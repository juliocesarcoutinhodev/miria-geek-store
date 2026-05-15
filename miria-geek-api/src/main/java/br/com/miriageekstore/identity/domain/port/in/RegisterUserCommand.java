package br.com.miriageekstore.identity.domain.port.in;

public record RegisterUserCommand(
        String fullName,
        String email,
        String rawPassword,
        String passwordConfirmation
) {}
