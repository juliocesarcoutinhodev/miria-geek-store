package br.com.miriageekstore.identity.domain.port.out;

public interface ResendRateLimiter {
    boolean isAllowed(String email);
    void record(String email);
}
