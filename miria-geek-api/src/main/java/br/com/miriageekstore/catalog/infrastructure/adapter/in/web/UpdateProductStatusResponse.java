package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import java.util.UUID;

record UpdateProductStatusResponse(UUID id, String name, String status) {}
