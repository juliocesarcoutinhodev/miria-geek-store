package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.exception.CurrentPasswordMismatchException;
import br.com.miriageekstore.identity.domain.exception.NewPasswordSameAsCurrentException;
import br.com.miriageekstore.identity.domain.exception.PasswordConfirmationException;
import br.com.miriageekstore.identity.domain.exception.UserNotFoundException;
import br.com.miriageekstore.identity.domain.model.Email;
import br.com.miriageekstore.identity.domain.model.FullName;
import br.com.miriageekstore.identity.domain.model.Password;
import br.com.miriageekstore.identity.domain.model.RefreshToken;
import br.com.miriageekstore.identity.domain.model.User;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.model.UserRole;
import br.com.miriageekstore.identity.domain.model.UserStatus;
import br.com.miriageekstore.identity.domain.port.in.ChangePasswordCommand;
import br.com.miriageekstore.identity.domain.port.out.JwtTokenService;
import br.com.miriageekstore.identity.domain.port.out.PasswordHasher;
import br.com.miriageekstore.identity.domain.port.out.RefreshTokenRepository;
import br.com.miriageekstore.identity.domain.port.out.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangePasswordUseCaseTest {

    @Mock UserRepository userRepository;
    @Mock PasswordHasher passwordHasher;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock JwtTokenService jwtTokenService;

    @InjectMocks ChangePasswordUseCaseImpl useCase;

    private static final ChangePasswordCommand CMD = new ChangePasswordCommand(
            "CurrentPass1", "NewPass2", "NewPass2", "127.0.0.1", "TestAgent");

    @Test
    void shouldChangePasswordAndReturnNewTokens() {
        var userId = UserId.generate();
        var user = activeUser(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordHasher.matches("CurrentPass1", "$2a$12$old")).thenReturn(true);
        when(passwordHasher.matches("NewPass2", "$2a$12$old")).thenReturn(false);
        when(passwordHasher.hash("NewPass2")).thenReturn("$2a$12$new");
        when(userRepository.save(any())).thenReturn(user);
        when(jwtTokenService.generateAccessToken(user)).thenReturn("new-access");
        when(jwtTokenService.generateRefreshToken()).thenReturn("new-refresh");

        var result = useCase.execute(userId, CMD);

        assertThat(result.accessToken()).isEqualTo("new-access");
        assertThat(result.refreshToken()).isEqualTo("new-refresh");
        verify(refreshTokenRepository).revokeAllByUserId(userId);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowWhenConfirmationMismatch() {
        var cmd = new ChangePasswordCommand("CurrentPass1", "NewPass2", "Different3", "127.0.0.1", "Agent");

        assertThatThrownBy(() -> useCase.execute(UserId.generate(), cmd))
                .isInstanceOf(PasswordConfirmationException.class);

        verify(userRepository, never()).findById(any());
    }

    @Test
    void shouldThrowWhenCurrentPasswordIsWrong() {
        var userId = UserId.generate();
        when(userRepository.findById(userId)).thenReturn(Optional.of(activeUser(userId)));
        when(passwordHasher.matches("CurrentPass1", "$2a$12$old")).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(userId, CMD))
                .isInstanceOf(CurrentPasswordMismatchException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenNewPasswordSameAsCurrent() {
        var userId = UserId.generate();
        when(userRepository.findById(userId)).thenReturn(Optional.of(activeUser(userId)));
        when(passwordHasher.matches(anyString(), anyString())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(userId, CMD))
                .isInstanceOf(NewPasswordSameAsCurrentException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        var userId = UserId.generate();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(userId, CMD))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldRevokeAllTokensBeforeGeneratingNew() {
        var userId = UserId.generate();
        var user = activeUser(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordHasher.matches("CurrentPass1", "$2a$12$old")).thenReturn(true);
        when(passwordHasher.matches("NewPass2", "$2a$12$old")).thenReturn(false);
        when(passwordHasher.hash(anyString())).thenReturn("$2a$12$new");
        when(userRepository.save(any())).thenReturn(user);
        when(jwtTokenService.generateAccessToken(any())).thenReturn("access");
        when(jwtTokenService.generateRefreshToken()).thenReturn("refresh");

        var inOrder = org.mockito.Mockito.inOrder(refreshTokenRepository);
        useCase.execute(userId, CMD);
        inOrder.verify(refreshTokenRepository).revokeAllByUserId(userId);
        inOrder.verify(refreshTokenRepository).save(any());
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private User activeUser(UserId userId) {
        return User.reconstitute(
                userId, FullName.of("User Test"), Email.of("user@email.com"),
                Password.fromHash("$2a$12$old"), UserStatus.ACTIVE,
                Set.of(UserRole.ROLE_CUSTOMER), null, Instant.now(), null, null);
    }
}
