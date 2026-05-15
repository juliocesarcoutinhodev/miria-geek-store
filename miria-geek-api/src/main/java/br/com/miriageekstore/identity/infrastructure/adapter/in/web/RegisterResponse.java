package br.com.miriageekstore.identity.infrastructure.adapter.in.web;

import br.com.miriageekstore.identity.domain.port.in.RegisterUserResult;

import java.time.Instant;
import java.util.UUID;

record RegisterResponse(
        UUID id,
        String fullName,
        String email,
        String status,
        Instant createdAt
) {
    static RegisterResponse from(RegisterUserResult result) {
        return new RegisterResponse(
                result.id(),
                result.fullName(),
                result.email(),
                result.status(),
                result.createdAt()
        );
    }
}
