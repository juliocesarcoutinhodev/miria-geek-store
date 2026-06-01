package br.com.miriageekstore.identity.infrastructure.adapter.in.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

record CreateAdminUserRequest(
        @NotBlank @Size(min = 3, max = 255) String fullName,
        @NotBlank @Email String email
) {}
