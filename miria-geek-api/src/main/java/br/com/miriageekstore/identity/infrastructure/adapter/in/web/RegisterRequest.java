package br.com.miriageekstore.identity.infrastructure.adapter.in.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

record RegisterRequest(

        @NotBlank(message = "Nome completo é obrigatório")
        @Size(min = 3, max = 255, message = "Nome completo deve ter entre 3 e 255 caracteres")
        String fullName,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        String password,

        @NotBlank(message = "Confirmação de senha é obrigatória")
        String passwordConfirmation
) {}
