package br.com.miriageekstore.identity.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VerificationTokenTest {

    @Test
    void shouldGenerateNonNullToken() {
        var token = VerificationToken.generate();
        assertThat(token.token()).isNotNull();
    }

    @Test
    void shouldGenerateUniqueTokens() {
        var t1 = VerificationToken.generate();
        var t2 = VerificationToken.generate();
        assertThat(t1.token()).isNotEqualTo(t2.token());
    }

    @Test
    void shouldNotBeExpiredWhenJustCreated() {
        var token = VerificationToken.generate();
        assertThat(token.isExpired()).isFalse();
    }

    @Test
    void shouldBeExpiredWhenExpiresAtIsInThePast() {
        var token = new VerificationToken(UUID.randomUUID(), Instant.now().minusSeconds(1));
        assertThat(token.isExpired()).isTrue();
    }

    @Test
    void shouldNotBeExpiredWhenExpiresAtIsInTheFuture() {
        var token = new VerificationToken(UUID.randomUUID(), Instant.now().plusSeconds(3600));
        assertThat(token.isExpired()).isFalse();
    }

    @Test
    void shouldExpireAfter24Hours() {
        var token = VerificationToken.generate();
        var twentyFourHoursLater = token.expiresAt().minusSeconds(1);
        assertThat(Instant.now().isBefore(twentyFourHoursLater)).isTrue();
    }
}
