package br.com.miriageekstore.identity.infrastructure.adapter.in.web;

import java.util.List;

record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}
