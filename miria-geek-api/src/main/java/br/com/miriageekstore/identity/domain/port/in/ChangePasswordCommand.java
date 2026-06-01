package br.com.miriageekstore.identity.domain.port.in;

public record ChangePasswordCommand(
        String currentPassword,
        String newPassword,
        String passwordConfirmation,
        String ipAddress,
        String userAgent
) {}
