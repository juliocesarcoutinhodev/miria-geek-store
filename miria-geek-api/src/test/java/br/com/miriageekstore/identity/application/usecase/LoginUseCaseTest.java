package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.event.UserLoggedIn;
import br.com.miriageekstore.identity.domain.exception.AccountLockedException;
import br.com.miriageekstore.identity.domain.exception.AccountNotActiveException;
import br.com.miriageekstore.identity.domain.exception.InvalidCredentialsException;
import br.com.miriageekstore.identity.domain.model.Email;
import br.com.miriageekstore.identity.domain.model.FullName;
import br.com.miriageekstore.identity.domain.model.Password;
import br.com.miriageekstore.identity.domain.model.User;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.model.UserRole;
import br.com.miriageekstore.identity.domain.model.UserStatus;
import br.com.miriageekstore.identity.domain.port.in.LoginCommand;
import br.com.miriageekstore.identity.domain.port.out.DomainEventPublisher;
import br.com.miriageekstore.identity.domain.port.out.JwtTokenService;
import br.com.miriageekstore.identity.domain.port.out.LoginAttemptTracker;
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
class LoginUseCaseTest {

    @Mock UserRepository userRepository;
    @Mock PasswordHasher passwordHasher;
    @Mock LoginAttemptTracker attemptTracker;
    @Mock JwtTokenService jwtTokenService;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock DomainEventPublisher eventPublisher;

    @InjectMocks
    LoginUseCaseImpl useCase;

    private static final LoginCommand CMD = new LoginCommand(
            "user@email.com", "secret123", "127.0.0.1", "TestAgent/1.0");

    @Test
    void shouldLoginSuccessfully() {
        var user = activeUser();
        when(attemptTracker.isLocked(CMD.email())).thenReturn(false);
        when(userRepository.findByEmail(Email.of(CMD.email()))).thenReturn(Optional.of(user));
        when(passwordHasher.matches(CMD.password(), user.getPassword().hash())).thenReturn(true);
        when(jwtTokenService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtTokenService.generateRefreshToken()).thenReturn("refresh-token");

        var result = useCase.execute(CMD);

        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.email()).isEqualTo("user@email.com");
        assertThat(result.roles()).contains("ROLE_CUSTOMER");
        verify(refreshTokenRepository).save(any());
        verify(eventPublisher).publish(any(UserLoggedIn.class));
        verify(attemptTracker).resetFailures(CMD.email());
    }

    @Test
    void shouldThrowWhenAccountLocked() {
        when(attemptTracker.isLocked(CMD.email())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(CMD))
                .isInstanceOf(AccountLockedException.class);

        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void shouldThrowAndRecordFailureWhenUserNotFound() {
        when(attemptTracker.isLocked(CMD.email())).thenReturn(false);
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(CMD))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(attemptTracker).recordFailure(CMD.email());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void shouldThrowAndRecordFailureWhenWrongPassword() {
        var user = activeUser();
        when(attemptTracker.isLocked(CMD.email())).thenReturn(false);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordHasher.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(CMD))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(attemptTracker).recordFailure(CMD.email());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void shouldThrowWhenAccountNotActive() {
        var user = pendingUser();
        when(attemptTracker.isLocked(CMD.email())).thenReturn(false);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordHasher.matches(anyString(), anyString())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(CMD))
                .isInstanceOf(AccountNotActiveException.class);

        verify(eventPublisher, never()).publish(any());
        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void shouldPublishEventAfterSavingRefreshToken() {
        var user = activeUser();
        when(attemptTracker.isLocked(CMD.email())).thenReturn(false);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordHasher.matches(anyString(), anyString())).thenReturn(true);
        when(jwtTokenService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtTokenService.generateRefreshToken()).thenReturn("refresh-token");

        var inOrder = org.mockito.Mockito.inOrder(refreshTokenRepository, eventPublisher);
        useCase.execute(CMD);
        inOrder.verify(refreshTokenRepository).save(any());
        inOrder.verify(eventPublisher).publish(any());
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private User activeUser() {
        return User.reconstitute(
                UserId.generate(),
                FullName.of("User Test"),
                Email.of("user@email.com"),
                Password.fromHash("$2a$12$hashed"),
                UserStatus.ACTIVE,
                Set.of(UserRole.ROLE_CUSTOMER),
                null,
                Instant.now()
        );
    }

    private User pendingUser() {
        return User.reconstitute(
                UserId.generate(),
                FullName.of("User Test"),
                Email.of("user@email.com"),
                Password.fromHash("$2a$12$hashed"),
                UserStatus.PENDING_VERIFICATION,
                Set.of(UserRole.ROLE_CUSTOMER),
                null,
                Instant.now()
        );
    }
}
