package br.com.miriageekstore.order.infrastructure.adapter.in.web;

import jakarta.validation.constraints.NotBlank;

record UpdateOrderStatusRequest(
        @NotBlank String status,
        String note
) {}
