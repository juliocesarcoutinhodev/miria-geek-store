package br.com.miriageekstore.identity.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserRegenerateTokenTest {

    @Test
    void shouldGenerateNewToken() {
        var originalToken = new VerificationToken(UUID.randomUUID(), Instant.now().plus(24, ChronoUnit.HOURS));
        var user = pendingUser(originalToken);

        user.regenerateVerificationToken();

        assertThat(user.getVerificationToken().token())
                .isNotEqualTo(originalToken.token());
    }

    @Test
    void shouldGenerateNonExpiredToken() {
        var expiredToken = new VerificationToken(UUID.randomUUID(), Instant.now().minus(1, ChronoUnit.HOURS));
        var user = pendingUser(expiredToken);

        user.regenerateVerificationToken();

        assertThat(user.getVerificationToken().isExpired()).isFalse();
    }

    @Test
    void shouldValidateNewTokenFor24Hours() {
        var user = pendingUser(new VerificationToken(UUID.randomUUID(), Instant.now().minus(1, ChronoUnit.HOURS)));

        user.regenerateVerificationToken();

        var expiresAt = user.getVerificationToken().expiresAt();
        assertThat(expiresAt).isAfter(Instant.now().plus(23, ChronoUnit.HOURS));
    }

    @Test
    void shouldReplaceExpiredToken() {
        var expiredToken = new VerificationToken(UUID.randomUUID(), Instant.now().minus(1, ChronoUnit.HOURS));
        var user = pendingUser(expiredToken);

        user.regenerateVerificationToken();

        assertThat(user.getVerificationToken().isExpired()).isFalse();
        assertThat(user.getVerificationToken().token()).isNotEqualTo(expiredToken.token());
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private User pendingUser(VerificationToken token) {
        return User.reconstitute(
                UserId.generate(),
                FullName.of("Maria Silva"),
                Email.of("maria@email.com"),
                Password.fromHash("$2a$12$hashedpassword"),
                UserStatus.PENDING_VERIFICATION,
                Set.of(UserRole.ROLE_CUSTOMER),
                token,
                Instant.now()
        );
    }
}
