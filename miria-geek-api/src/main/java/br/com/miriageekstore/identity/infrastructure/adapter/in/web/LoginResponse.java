package br.com.miriageekstore.identity.infrastructure.adapter.in.web;

import java.util.Set;
import java.util.UUID;

record LoginResponse(UUID id, String name, String email, Set<String> roles, String status) {}
