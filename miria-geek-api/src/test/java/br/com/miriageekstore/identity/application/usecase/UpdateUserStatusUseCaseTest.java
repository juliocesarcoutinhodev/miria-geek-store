package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.exception.CannotDeactivateOwnAccountException;
import br.com.miriageekstore.identity.domain.exception.UserNotFoundException;
import br.com.miriageekstore.identity.domain.model.Email;
import br.com.miriageekstore.identity.domain.model.FullName;
import br.com.miriageekstore.identity.domain.model.Password;
import br.com.miriageekstore.identity.domain.model.User;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.model.UserRole;
import br.com.miriageekstore.identity.domain.model.UserStatus;
import br.com.miriageekstore.identity.domain.port.in.UpdateUserStatusCommand;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateUserStatusUseCaseTest {

    @Mock UserRepository userRepository;
    @Mock RefreshTokenRepository refreshTokenRepository;

    @InjectMocks UpdateUserStatusUseCaseImpl useCase;

    @Test
    void shouldDeactivateUserAndRevokeTokens() {
        var requesterId = UserId.generate();
        var targetId = UserId.generate();
        var user = customerUser(targetId);
        when(userRepository.findById(targetId)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        var result = useCase.execute(new UpdateUserStatusCommand(targetId, UserStatus.INACTIVE, requesterId));

        assertThat(result.status()).isEqualTo("INACTIVE");
        verify(refreshTokenRepository).revokeAllByUserId(targetId);
        verify(userRepository).save(user);
    }

    @Test
    void shouldActivateUserWithoutRevokingTokens() {
        var requesterId = UserId.generate();
        var targetId = UserId.generate();
        var user = inactiveUser(targetId);
        when(userRepository.findById(targetId)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        var result = useCase.execute(new UpdateUserStatusCommand(targetId, UserStatus.ACTIVE, requesterId));

        assertThat(result.status()).isEqualTo("ACTIVE");
        verify(refreshTokenRepository, never()).revokeAllByUserId(any());
    }

    @Test
    void shouldThrowWhenAdminTriesToDeactivateOwnAccount() {
        var adminId = UserId.generate();

        assertThatThrownBy(() -> useCase.execute(
                new UpdateUserStatusCommand(adminId, UserStatus.INACTIVE, adminId)))
                .isInstanceOf(CannotDeactivateOwnAccountException.class);

        verify(userRepository, never()).findById(any());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        var requesterId = UserId.generate();
        var targetId = UserId.generate();
        when(userRepository.findById(targetId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(
                new UpdateUserStatusCommand(targetId, UserStatus.INACTIVE, requesterId)))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldThrowWhenStatusIsInvalid() {
        var requesterId = UserId.generate();
        var targetId = UserId.generate();

        assertThatThrownBy(() -> useCase.execute(
                new UpdateUserStatusCommand(targetId, UserStatus.PENDING_VERIFICATION, requesterId)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private User customerUser(UserId userId) {
        return User.reconstitute(userId, FullName.of("Maria Test"), Email.of("maria@email.com"),
                Password.fromHash("$2a$12$hash"), UserStatus.ACTIVE,
                Set.of(UserRole.ROLE_CUSTOMER), null, Instant.now(), null);
    }

    private User inactiveUser(UserId userId) {
        return User.reconstitute(userId, FullName.of("Maria Test"), Email.of("maria@email.com"),
                Password.fromHash("$2a$12$hash"), UserStatus.INACTIVE,
                Set.of(UserRole.ROLE_CUSTOMER), null, Instant.now(), null);
    }
}
