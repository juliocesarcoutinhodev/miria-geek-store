package br.com.miriageekstore.identity.infrastructure.adapter.in.web;

import jakarta.validation.constraints.NotBlank;

record ChangePasswordRequest(
        @NotBlank String currentPassword,
        @NotBlank String newPassword,
        @NotBlank String passwordConfirmation
) {}
