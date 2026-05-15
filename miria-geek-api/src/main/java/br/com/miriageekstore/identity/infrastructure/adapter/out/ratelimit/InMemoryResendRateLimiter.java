package br.com.miriageekstore.identity.infrastructure.adapter.out.ratelimit;

import br.com.miriageekstore.identity.domain.port.out.ResendRateLimiter;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
class InMemoryResendRateLimiter implements ResendRateLimiter {

    private static final int MAX_ATTEMPTS = 3;
    private static final Duration WINDOW = Duration.ofHours(1);

    private final ConcurrentHashMap<String, List<Instant>> attempts = new ConcurrentHashMap<>();

    @Override
    public boolean isAllowed(String email) {
        var windowStart = Instant.now().minus(WINDOW);
        return attemptsInWindow(email, windowStart) < MAX_ATTEMPTS;
    }

    @Override
    public void record(String email) {
        var now = Instant.now();
        var windowStart = now.minus(WINDOW);
        attempts.compute(email, (k, existing) -> {
            var list = existing == null ? new ArrayList<Instant>() : new ArrayList<>(existing);
            list.removeIf(t -> !t.isAfter(windowStart));
            list.add(now);
            return list;
        });
    }

    private long attemptsInWindow(String email, Instant windowStart) {
        return attempts.getOrDefault(email, List.of())
                .stream()
                .filter(t -> t.isAfter(windowStart))
                .count();
    }
}
