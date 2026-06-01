package br.com.miriageekstore.identity.infrastructure.adapter.in.web;

import java.util.UUID;

record UserStatusResponse(UUID id, String fullName, String email, String status) {}
