package br.com.miriageekstore.identity.infrastructure.adapter.in.web;

import br.com.miriageekstore.identity.domain.model.UserStatus;
import jakarta.validation.constraints.NotNull;

record UpdateUserStatusRequest(@NotNull UserStatus status) {}
