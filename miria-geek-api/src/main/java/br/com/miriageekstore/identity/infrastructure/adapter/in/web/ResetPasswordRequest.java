package br.com.miriageekstore.identity.infrastructure.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

record ResetPasswordRequest(
        @NotNull UUID token,
        @NotBlank String newPassword,
        @NotBlank String passwordConfirmation
) {}
