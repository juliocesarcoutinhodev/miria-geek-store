package br.com.miriageekstore.identity.domain.model;

import br.com.miriageekstore.identity.domain.exception.UserAlreadyVerifiedException;
import br.com.miriageekstore.identity.domain.exception.VerificationTokenExpiredException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserVerifyEmailTest {

    @Test
    void shouldActivateAccountWhenTokenIsValid() {
        var user = pendingUserWithToken(validToken());

        user.verifyEmail();

        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void shouldThrowWhenAlreadyVerified() {
        var user = activeUser();

        assertThatThrownBy(user::verifyEmail)
                .isInstanceOf(UserAlreadyVerifiedException.class)
                .hasMessage("Email has already been verified");
    }

    @Test
    void shouldThrowWhenTokenIsExpired() {
        var user = pendingUserWithToken(expiredToken());

        assertThatThrownBy(user::verifyEmail)
                .isInstanceOf(VerificationTokenExpiredException.class)
                .hasMessage("Verification token has expired. Please request a new verification email.");
    }

    @Test
    void shouldNotChangeStatusWhenAlreadyVerified() {
        var user = activeUser();

        assertThatThrownBy(user::verifyEmail)
                .isInstanceOf(UserAlreadyVerifiedException.class);

        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private User pendingUserWithToken(VerificationToken token) {
        return User.reconstitute(
                UserId.generate(),
                FullName.of("Maria Silva"),
                Email.of("maria@email.com"),
                Password.fromHash("$2a$12$hashedpassword"),
                UserStatus.PENDING_VERIFICATION,
                Set.of(UserRole.ROLE_CUSTOMER),
                token,
                Instant.now(), null
        );
    }

    private User activeUser() {
        return User.reconstitute(
                UserId.generate(),
                FullName.of("Maria Silva"),
                Email.of("maria@email.com"),
                Password.fromHash("$2a$12$hashedpassword"),
                UserStatus.ACTIVE,
                Set.of(UserRole.ROLE_CUSTOMER),
                validToken(),
                Instant.now(), null
        );
    }

    private VerificationToken validToken() {
        return new VerificationToken(UUID.randomUUID(), Instant.now().plus(24, ChronoUnit.HOURS));
    }

    private VerificationToken expiredToken() {
        return new VerificationToken(UUID.randomUUID(), Instant.now().minus(1, ChronoUnit.HOURS));
    }
}
