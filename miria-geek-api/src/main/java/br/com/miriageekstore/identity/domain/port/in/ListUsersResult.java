package br.com.miriageekstore.identity.domain.port.in;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ListUsersResult(
        List<UserSummary> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public record UserSummary(
            UUID id,
            String fullName,
            String email,
            String role,
            String status,
            Instant createdAt
    ) {}
}
