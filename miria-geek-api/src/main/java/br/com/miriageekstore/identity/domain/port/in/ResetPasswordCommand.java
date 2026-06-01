package br.com.miriageekstore.identity.domain.port.in;

import java.util.UUID;

public record ResetPasswordCommand(UUID token, String newPassword, String passwordConfirmation) {}
