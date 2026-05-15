package br.com.miriageekstore.identity.domain.port.out;

public interface LoginAttemptTracker {
    boolean isLocked(String email);
    void recordFailure(String email);
    void resetFailures(String email);
}
