package br.com.miriageekstore.order.infrastructure.adapter.in.web;

import java.util.List;

record AdminOrderPageResponse(
        List<AdminOrderSummaryResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}
