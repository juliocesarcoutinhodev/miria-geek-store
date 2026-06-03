package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import java.util.List;

record AdminProductPageResponse(
        List<AdminProductSummaryResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}
