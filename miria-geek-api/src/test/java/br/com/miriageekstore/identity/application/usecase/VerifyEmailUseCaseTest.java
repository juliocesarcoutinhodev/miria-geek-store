package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.event.UserEmailVerified;
import br.com.miriageekstore.identity.domain.exception.UserAlreadyVerifiedException;
import br.com.miriageekstore.identity.domain.exception.VerificationTokenExpiredException;
import br.com.miriageekstore.identity.domain.exception.VerificationTokenNotFoundException;
import br.com.miriageekstore.identity.domain.model.Email;
import br.com.miriageekstore.identity.domain.model.FullName;
import br.com.miriageekstore.identity.domain.model.Password;
import br.com.miriageekstore.identity.domain.model.User;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.model.UserRole;
import br.com.miriageekstore.identity.domain.model.UserStatus;
import br.com.miriageekstore.identity.domain.model.VerificationToken;
import br.com.miriageekstore.identity.domain.port.out.DomainEventPublisher;
import br.com.miriageekstore.identity.domain.port.out.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerifyEmailUseCaseTest {

    @Mock
    UserRepository userRepository;

    @Mock
    DomainEventPublisher eventPublisher;

    @InjectMocks
    VerifyEmailUseCaseImpl useCase;

    @Test
    void shouldVerifyEmailSuccessfully() {
        var token = UUID.randomUUID();
        var user = pendingUserWithToken(token, Instant.now().plus(24, ChronoUnit.HOURS));
        when(userRepository.findByVerificationToken(token)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        useCase.execute(token);

        verify(userRepository).save(user);
        verify(eventPublisher).publish(any(UserEmailVerified.class));
    }

    @Test
    void shouldSaveBeforePublishingEvent() {
        var token = UUID.randomUUID();
        var user = pendingUserWithToken(token, Instant.now().plus(24, ChronoUnit.HOURS));
        when(userRepository.findByVerificationToken(token)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        var inOrder = org.mockito.Mockito.inOrder(userRepository, eventPublisher);

        useCase.execute(token);

        inOrder.verify(userRepository).save(any());
        inOrder.verify(eventPublisher).publish(any());
    }

    @Test
    void shouldThrowWhenTokenNotFound() {
        var token = UUID.randomUUID();
        when(userRepository.findByVerificationToken(token)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(token))
                .isInstanceOf(VerificationTokenNotFoundException.class);

        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void shouldThrowWhenAlreadyVerified() {
        var token = UUID.randomUUID();
        var user = activeUserWithToken(token);
        when(userRepository.findByVerificationToken(token)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> useCase.execute(token))
                .isInstanceOf(UserAlreadyVerifiedException.class);

        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void shouldThrowWhenTokenExpired() {
        var token = UUID.randomUUID();
        var user = pendingUserWithToken(token, Instant.now().minus(1, ChronoUnit.HOURS));
        when(userRepository.findByVerificationToken(token)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> useCase.execute(token))
                .isInstanceOf(VerificationTokenExpiredException.class);

        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any());
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private User pendingUserWithToken(UUID tokenValue, Instant expiresAt) {
        return User.reconstitute(
                UserId.generate(),
                FullName.of("Maria Silva"),
                Email.of("maria@email.com"),
                Password.fromHash("$2a$12$hashedpassword"),
                UserStatus.PENDING_VERIFICATION,
                Set.of(UserRole.ROLE_CUSTOMER),
                new VerificationToken(tokenValue, expiresAt),
                Instant.now()
        );
    }

    private User activeUserWithToken(UUID tokenValue) {
        return User.reconstitute(
                UserId.generate(),
                FullName.of("Maria Silva"),
                Email.of("maria@email.com"),
                Password.fromHash("$2a$12$hashedpassword"),
                UserStatus.ACTIVE,
                Set.of(UserRole.ROLE_CUSTOMER),
                new VerificationToken(tokenValue, Instant.now().plus(24, ChronoUnit.HOURS)),
                Instant.now()
        );
    }
}
