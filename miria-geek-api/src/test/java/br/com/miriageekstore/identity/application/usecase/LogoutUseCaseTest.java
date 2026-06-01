package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.model.RefreshToken;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.port.out.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogoutUseCaseTest {

    @Mock RefreshTokenRepository refreshTokenRepository;

    @InjectMocks LogoutUseCaseImpl useCase;

    @Test
    void shouldRevokeActiveToken() {
        var token = activeToken();
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(token));

        useCase.execute("raw-token");

        verify(refreshTokenRepository).save(argThat(RefreshToken::revoked));
    }

    @Test
    void shouldNotSaveWhenTokenAlreadyRevoked() {
        var token = revokedToken();
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(token));

        useCase.execute("raw-token");

        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void shouldSucceedWhenTokenNotFound() {
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.empty());

        useCase.execute("raw-token");

        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void shouldSucceedWhenRawTokenIsNull() {
        useCase.execute(null);

        verify(refreshTokenRepository, never()).findByTokenHash(any());
        verify(refreshTokenRepository, never()).save(any());
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private RefreshToken activeToken() {
        var id = UUID.randomUUID();
        return new RefreshToken(id, UserId.generate(), "hash",
                Instant.now().plusSeconds(3600), "127.0.0.1", "Agent",
                Instant.now(), id, false, null);
    }

    private RefreshToken revokedToken() {
        var id = UUID.randomUUID();
        return new RefreshToken(id, UserId.generate(), "hash",
                Instant.now().plusSeconds(3600), "127.0.0.1", "Agent",
                Instant.now(), id, true, Instant.now().minusSeconds(60));
    }
}
