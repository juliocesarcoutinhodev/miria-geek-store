package br.com.miriageekstore.identity.domain.port.in;

public record ListUsersQuery(
        String name,
        String email,
        String status,
        String role,
        int page,
        int size,
        String sort,
        String direction
) {}
