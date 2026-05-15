package br.com.miriageekstore.identity.infrastructure.adapter.in.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

record RegisterRequest(

        @NotBlank(message = "Full name is required")
        @Size(min = 3, max = 255, message = "Full name must be between 3 and 255 characters")
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        String password,

        @NotBlank(message = "Password confirmation is required")
        String passwordConfirmation
) {}
