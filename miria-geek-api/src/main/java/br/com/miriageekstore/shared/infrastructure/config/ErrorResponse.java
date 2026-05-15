package br.com.miriageekstore.shared.infrastructure.config;

import java.time.Instant;

public record ErrorResponse(
        String code,
        String message,
        Instant timestamp
) {
    public ErrorResponse(String code, String message) {
        this(code, message, Instant.now());
    }
}
