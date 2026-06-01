package br.com.miriageekstore.identity.infrastructure.adapter.in.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

record PatchUserRequest(
        @Size(min = 3, max = 255) String fullName,
        @Email String email,
        @Pattern(regexp = "ROLE_CUSTOMER|ROLE_ADMIN") String role
) {}
