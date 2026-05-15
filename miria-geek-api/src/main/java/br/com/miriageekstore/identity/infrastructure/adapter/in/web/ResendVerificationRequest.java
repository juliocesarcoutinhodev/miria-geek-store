package br.com.miriageekstore.identity.infrastructure.adapter.in.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

record ResendVerificationRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email
) {}
