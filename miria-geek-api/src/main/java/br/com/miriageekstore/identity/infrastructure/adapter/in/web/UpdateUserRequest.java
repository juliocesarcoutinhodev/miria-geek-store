package br.com.miriageekstore.identity.infrastructure.adapter.in.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

record UpdateUserRequest(
        @NotBlank @Size(min = 3, max = 255) String fullName,
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "ROLE_CUSTOMER|ROLE_ADMIN") String role
) {}
