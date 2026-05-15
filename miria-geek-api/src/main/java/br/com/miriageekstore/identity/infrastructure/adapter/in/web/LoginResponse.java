package br.com.miriageekstore.identity.infrastructure.adapter.in.web;

import java.util.Set;

record LoginResponse(String email, Set<String> roles) {}
