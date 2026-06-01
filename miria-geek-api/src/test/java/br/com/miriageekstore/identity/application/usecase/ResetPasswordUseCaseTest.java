package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.exception.PasswordConfirmationException;
import br.com.miriageekstore.identity.domain.exception.PasswordResetTokenAlreadyUsedException;
import br.com.miriageekstore.identity.domain.exception.PasswordResetTokenExpiredException;
import br.com.miriageekstore.identity.domain.exception.PasswordResetTokenNotFoundException;
import br.com.miriageekstore.identity.domain.model.Email;
import br.com.miriageekstore.identity.domain.model.FullName;
import br.com.miriageekstore.identity.domain.model.Password;
import br.com.miriageekstore.identity.domain.model.PasswordResetToken;
import br.com.miriageekstore.identity.domain.model.User;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.model.UserRole;
import br.com.miriageekstore.identity.domain.model.UserStatus;
import br.com.miriageekstore.identity.domain.port.in.ResetPasswordCommand;
import br.com.miriageekstore.identity.domain.port.out.PasswordHasher;
import br.com.miriageekstore.identity.domain.port.out.PasswordResetTokenRepository;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResetPasswordUseCaseTest {

    @Mock PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock UserRepository userRepository;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock PasswordHasher passwordHasher;

    @InjectMocks ResetPasswordUseCaseImpl useCase;

    @Test
    void shouldResetPasswordSuccessfully() {
        var userId = UserId.generate();
        var token = validToken(userId);
        var user = activeUser(userId);
        when(passwordResetTokenRepository.findByToken(token.token())).thenReturn(Optional.of(token));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordHasher.hash(any())).thenReturn("$2a$12$newhash");
        when(passwordResetTokenRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(userRepository.save(any())).thenReturn(user);

        useCase.execute(new ResetPasswordCommand(token.token(), "NewPass1", "NewPass1"));

        verify(passwordResetTokenRepository).save(argThat(PasswordResetToken::used));
        verify(userRepository).save(user);
        verify(refreshTokenRepository).revokeAllByUserId(userId);
        assertThat(user.getPassword().hash()).isEqualTo("$2a$12$newhash");
    }

    @Test
    void shouldThrowWhenTokenNotFound() {
        var tokenUUID = UUID.randomUUID();
        when(passwordResetTokenRepository.findByToken(tokenUUID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new ResetPasswordCommand(tokenUUID, "NewPass1", "NewPass1")))
                .isInstanceOf(PasswordResetTokenNotFoundException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenTokenAlreadyUsed() {
        var userId = UserId.generate();
        var token = usedToken(userId);
        when(passwordResetTokenRepository.findByToken(token.token())).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> useCase.execute(new ResetPasswordCommand(token.token(), "NewPass1", "NewPass1")))
                .isInstanceOf(PasswordResetTokenAlreadyUsedException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenTokenExpired() {
        var userId = UserId.generate();
        var token = expiredToken(userId);
        when(passwordResetTokenRepository.findByToken(token.token())).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> useCase.execute(new ResetPasswordCommand(token.token(), "NewPass1", "NewPass1")))
                .isInstanceOf(PasswordResetTokenExpiredException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenPasswordConfirmationMismatch() {
        var tokenUUID = UUID.randomUUID();

        assertThatThrownBy(() -> useCase.execute(new ResetPasswordCommand(tokenUUID, "NewPass1", "Different1")))
                .isInstanceOf(PasswordConfirmationException.class);

        verify(passwordResetTokenRepository, never()).findByToken(any());
    }

    @Test
    void shouldRevokeAllSessionsAfterReset() {
        var userId = UserId.generate();
        var token = validToken(userId);
        var user = activeUser(userId);
        when(passwordResetTokenRepository.findByToken(token.token())).thenReturn(Optional.of(token));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordHasher.hash(any())).thenReturn("$2a$12$newhash");
        when(passwordResetTokenRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(userRepository.save(any())).thenReturn(user);

        useCase.execute(new ResetPasswordCommand(token.token(), "NewPass1", "NewPass1"));

        verify(refreshTokenRepository).revokeAllByUserId(userId);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private PasswordResetToken validToken(UserId userId) {
        return new PasswordResetToken(UUID.randomUUID(), userId, UUID.randomUUID(),
                Instant.now().plusSeconds(3600), false, null, Instant.now());
    }

    private PasswordResetToken usedToken(UserId userId) {
        return new PasswordResetToken(UUID.randomUUID(), userId, UUID.randomUUID(),
                Instant.now().plusSeconds(3600), true, Instant.now().minusSeconds(60), Instant.now().minusSeconds(120));
    }

    private PasswordResetToken expiredToken(UserId userId) {
        return new PasswordResetToken(UUID.randomUUID(), userId, UUID.randomUUID(),
                Instant.now().minusSeconds(1), false, null, Instant.now().minusSeconds(3601));
    }

    private User activeUser(UserId userId) {
        return User.reconstitute(userId, FullName.of("User Test"), Email.of("user@email.com"),
                Password.fromHash("$2a$12$oldhash"), UserStatus.ACTIVE,
                Set.of(UserRole.ROLE_CUSTOMER), null, Instant.now());
    }
}
