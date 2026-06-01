package br.com.miriageekstore.identity.infrastructure.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

record AddressRequest(
        @NotBlank @Size(max = 100) String alias,
        @NotBlank @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP inválido") String zipCode,
        @NotBlank @Size(max = 255) String street,
        @NotBlank @Size(max = 20) String number,
        @Size(max = 100) String complement,
        @NotBlank @Size(max = 100) String neighborhood,
        @NotBlank @Size(max = 100) String city,
        @NotBlank @Pattern(regexp = "[A-Z]{2}", message = "Estado deve ter 2 letras maiúsculas") String state,
        boolean isDefault
) {}
