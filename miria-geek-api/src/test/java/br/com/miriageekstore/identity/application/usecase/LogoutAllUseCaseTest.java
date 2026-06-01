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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogoutAllUseCaseTest {

    @Mock RefreshTokenRepository refreshTokenRepository;

    @InjectMocks LogoutAllUseCaseImpl useCase;

    @Test
    void shouldRevokeAllTokensForUser() {
        var userId = UserId.generate();
        var token = activeToken(userId);
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(token));

        useCase.execute("raw-token");

        verify(refreshTokenRepository).revokeAllByUserId(userId);
    }

    @Test
    void shouldSucceedWhenTokenNotFound() {
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.empty());

        useCase.execute("raw-token");

        verify(refreshTokenRepository, never()).revokeAllByUserId(any());
    }

    @Test
    void shouldSucceedWhenRawTokenIsNull() {
        useCase.execute(null);

        verify(refreshTokenRepository, never()).findByTokenHash(any());
        verify(refreshTokenRepository, never()).revokeAllByUserId(any());
    }

    @Test
    void shouldRevokeEvenWhenTokenAlreadyRevoked() {
        var userId = UserId.generate();
        var token = revokedToken(userId);
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(token));

        useCase.execute("raw-token");

        // revokeAllByUserId ainda é chamado: o usuário quer deslogar de todos os dispositivos
        verify(refreshTokenRepository).revokeAllByUserId(userId);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private RefreshToken activeToken(UserId userId) {
        var id = UUID.randomUUID();
        return new RefreshToken(id, userId, "hash",
                Instant.now().plusSeconds(3600), "127.0.0.1", "Agent",
                Instant.now(), id, false, null);
    }

    private RefreshToken revokedToken(UserId userId) {
        var id = UUID.randomUUID();
        return new RefreshToken(id, userId, "hash",
                Instant.now().plusSeconds(3600), "127.0.0.1", "Agent",
                Instant.now(), id, true, Instant.now().minusSeconds(60));
    }
}
