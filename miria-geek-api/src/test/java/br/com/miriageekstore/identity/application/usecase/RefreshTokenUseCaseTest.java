package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.exception.InvalidRefreshTokenException;
import br.com.miriageekstore.identity.domain.model.Email;
import br.com.miriageekstore.identity.domain.model.FullName;
import br.com.miriageekstore.identity.domain.model.Password;
import br.com.miriageekstore.identity.domain.model.RefreshToken;
import br.com.miriageekstore.identity.domain.model.User;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.model.UserRole;
import br.com.miriageekstore.identity.domain.model.UserStatus;
import br.com.miriageekstore.identity.domain.port.out.JwtTokenService;
import br.com.miriageekstore.identity.domain.port.out.RefreshTokenRepository;
import br.com.miriageekstore.identity.domain.port.out.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUseCaseTest {

    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock UserRepository userRepository;
    @Mock JwtTokenService jwtTokenService;

    @InjectMocks
    RefreshTokenUseCaseImpl useCase;

    private static final String RAW_TOKEN = "raw-token-value";
    private static final String IP = "127.0.0.1";
    private static final String UA = "TestAgent/1.0";

    @Test
    void shouldRotateTokensOnSuccess() {
        var familyId = UUID.randomUUID();
        var userId = UserId.generate();
        var token = validToken(userId, familyId);
        var user = activeUser(userId);

        when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(token));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtTokenService.generateAccessToken(user)).thenReturn("new-access");
        when(jwtTokenService.generateRefreshToken()).thenReturn("new-refresh");

        var result = useCase.execute(RAW_TOKEN, IP, UA);

        assertThat(result.accessToken()).isEqualTo("new-access");
        assertThat(result.refreshToken()).isEqualTo("new-refresh");
        assertThat(result.email()).isEqualTo("user@email.com");
        assertThat(result.status()).isEqualTo("ACTIVE");

        var captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository, times(2)).save(captor.capture());

        var revokedToken = captor.getAllValues().get(0);
        var newToken = captor.getAllValues().get(1);

        assertThat(revokedToken.revoked()).isTrue();
        assertThat(newToken.revoked()).isFalse();
        assertThat(newToken.familyId()).isEqualTo(familyId);
    }

    @Test
    void shouldThrowWhenTokenNotFound() {
        when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(RAW_TOKEN, IP, UA))
                .isInstanceOf(InvalidRefreshTokenException.class);

        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void shouldRevokeEntireFamilyOnReuseDetection() {
        var familyId = UUID.randomUUID();
        var token = revokedToken(UserId.generate(), familyId);

        when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> useCase.execute(RAW_TOKEN, IP, UA))
                .isInstanceOf(InvalidRefreshTokenException.class);

        verify(refreshTokenRepository).revokeAllByFamilyId(familyId);
        verify(userRepository, never()).findById(any());
    }

    @Test
    void shouldRevokeAndThrowWhenTokenExpired() {
        var token = expiredToken(UserId.generate(), UUID.randomUUID());

        when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> useCase.execute(RAW_TOKEN, IP, UA))
                .isInstanceOf(InvalidRefreshTokenException.class);

        var captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());
        assertThat(captor.getValue().revoked()).isTrue();

        verify(userRepository, never()).findById(any());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        var userId = UserId.generate();
        var token = validToken(userId, UUID.randomUUID());

        when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(token));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(RAW_TOKEN, IP, UA))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private RefreshToken validToken(UserId userId, UUID familyId) {
        return new RefreshToken(UUID.randomUUID(), userId, "hash",
                Instant.now().plusSeconds(3600), IP, UA, Instant.now(),
                familyId, false, null);
    }

    private RefreshToken revokedToken(UserId userId, UUID familyId) {
        return new RefreshToken(UUID.randomUUID(), userId, "hash",
                Instant.now().plusSeconds(3600), IP, UA, Instant.now(),
                familyId, true, Instant.now());
    }

    private RefreshToken expiredToken(UserId userId, UUID familyId) {
        return new RefreshToken(UUID.randomUUID(), userId, "hash",
                Instant.now().minusSeconds(1), IP, UA, Instant.now(),
                familyId, false, null);
    }

    private User activeUser(UserId userId) {
        return User.reconstitute(
                userId,
                FullName.of("User Test"),
                Email.of("user@email.com"),
                Password.fromHash("$2a$12$hashed"),
                UserStatus.ACTIVE,
                Set.of(UserRole.ROLE_CUSTOMER),
                null,
                Instant.now(), null
        );
    }
}
