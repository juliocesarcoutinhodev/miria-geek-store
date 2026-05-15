package br.com.miriageekstore.identity.infrastructure.adapter.out.ratelimit;

import br.com.miriageekstore.identity.domain.port.out.LoginAttemptTracker;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Component
class InMemoryLoginAttemptTracker implements LoginAttemptTracker {

    private static final int MAX_FAILURES = 5;
    private static final long LOCKOUT_SECONDS = 15 * 60;

    private record AttemptInfo(int count, Instant lockedUntil) {}

    private final ConcurrentHashMap<String, AttemptInfo> attempts = new ConcurrentHashMap<>();

    @Override
    public boolean isLocked(String email) {
        var info = attempts.get(email);
        if (info == null) return false;
        if (info.lockedUntil() != null && Instant.now().isBefore(info.lockedUntil())) return true;
        if (info.lockedUntil() != null && Instant.now().isAfter(info.lockedUntil())) {
            attempts.remove(email);
        }
        return false;
    }

    @Override
    public void recordFailure(String email) {
        attempts.compute(email, (k, existing) -> {
            var count = existing == null ? 1 : existing.count() + 1;
            var lockedUntil = count >= MAX_FAILURES
                    ? Instant.now().plusSeconds(LOCKOUT_SECONDS)
                    : null;
            return new AttemptInfo(count, lockedUntil);
        });
    }

    @Override
    public void resetFailures(String email) {
        attempts.remove(email);
    }
}
