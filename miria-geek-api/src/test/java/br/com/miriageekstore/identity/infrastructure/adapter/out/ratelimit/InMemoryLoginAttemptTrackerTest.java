package br.com.miriageekstore.identity.infrastructure.adapter.out.ratelimit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryLoginAttemptTrackerTest {

    InMemoryLoginAttemptTracker tracker;

    @BeforeEach
    void setUp() {
        tracker = new InMemoryLoginAttemptTracker();
    }

    @Test
    void shouldNotBeLockedInitially() {
        assertThat(tracker.isLocked("user@email.com")).isFalse();
    }

    @Test
    void shouldNotLockBeforeMaxFailures() {
        for (int i = 0; i < 4; i++) {
            tracker.recordFailure("user@email.com");
        }
        assertThat(tracker.isLocked("user@email.com")).isFalse();
    }

    @Test
    void shouldLockAfterFiveFailures() {
        for (int i = 0; i < 5; i++) {
            tracker.recordFailure("user@email.com");
        }
        assertThat(tracker.isLocked("user@email.com")).isTrue();
    }

    @Test
    void shouldResetAfterSuccess() {
        for (int i = 0; i < 5; i++) {
            tracker.recordFailure("user@email.com");
        }
        tracker.resetFailures("user@email.com");
        assertThat(tracker.isLocked("user@email.com")).isFalse();
    }

    @Test
    void shouldTrackDifferentEmailsIndependently() {
        for (int i = 0; i < 5; i++) {
            tracker.recordFailure("locked@email.com");
        }
        assertThat(tracker.isLocked("locked@email.com")).isTrue();
        assertThat(tracker.isLocked("other@email.com")).isFalse();
    }
}
