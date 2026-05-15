package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.event.UserVerificationResent;
import br.com.miriageekstore.identity.domain.model.Email;
import br.com.miriageekstore.identity.domain.model.FullName;
import br.com.miriageekstore.identity.domain.model.Password;
import br.com.miriageekstore.identity.domain.model.User;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.model.UserRole;
import br.com.miriageekstore.identity.domain.model.UserStatus;
import br.com.miriageekstore.identity.domain.model.VerificationToken;
import br.com.miriageekstore.identity.domain.port.out.DomainEventPublisher;
import br.com.miriageekstore.identity.domain.port.out.ResendRateLimiter;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResendVerificationUseCaseTest {

    @Mock
    UserRepository userRepository;

    @Mock
    DomainEventPublisher eventPublisher;

    @Mock
    ResendRateLimiter rateLimiter;

    @InjectMocks
    ResendVerificationUseCaseImpl useCase;

    private static final String EMAIL = "maria@email.com";

    @Test
    void shouldResendVerificationForPendingUser() {
        var user = pendingUser();
        when(userRepository.findByEmail(Email.of(EMAIL))).thenReturn(Optional.of(user));
        when(rateLimiter.isAllowed(EMAIL)).thenReturn(true);
        when(userRepository.save(any())).thenReturn(user);

        useCase.execute(EMAIL);

        verify(rateLimiter).record(EMAIL);
        verify(userRepository).save(user);
        verify(eventPublisher).publish(any(UserVerificationResent.class));
    }

    @Test
    void shouldSaveBeforePublishingEvent() {
        var user = pendingUser();
        when(userRepository.findByEmail(Email.of(EMAIL))).thenReturn(Optional.of(user));
        when(rateLimiter.isAllowed(EMAIL)).thenReturn(true);
        when(userRepository.save(any())).thenReturn(user);

        var inOrder = org.mockito.Mockito.inOrder(userRepository, eventPublisher);

        useCase.execute(EMAIL);

        inOrder.verify(userRepository).save(any());
        inOrder.verify(eventPublisher).publish(any());
    }

    @Test
    void shouldSilentlySkipWhenEmailNotFound() {
        when(userRepository.findByEmail(Email.of(EMAIL))).thenReturn(Optional.empty());

        useCase.execute(EMAIL);

        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any());
        verify(rateLimiter, never()).isAllowed(any());
    }

    @Test
    void shouldSilentlySkipWhenUserIsActive() {
        var user = activeUser();
        when(userRepository.findByEmail(Email.of(EMAIL))).thenReturn(Optional.of(user));

        useCase.execute(EMAIL);

        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any());
        verify(rateLimiter, never()).isAllowed(any());
    }

    @Test
    void shouldSilentlySkipWhenRateLimitExceeded() {
        var user = pendingUser();
        when(userRepository.findByEmail(Email.of(EMAIL))).thenReturn(Optional.of(user));
        when(rateLimiter.isAllowed(EMAIL)).thenReturn(false);

        useCase.execute(EMAIL);

        verify(rateLimiter, never()).record(any());
        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void shouldRecordAttemptBeforeSaving() {
        var user = pendingUser();
        when(userRepository.findByEmail(Email.of(EMAIL))).thenReturn(Optional.of(user));
        when(rateLimiter.isAllowed(EMAIL)).thenReturn(true);
        when(userRepository.save(any())).thenReturn(user);

        var inOrder = org.mockito.Mockito.inOrder(rateLimiter, userRepository);

        useCase.execute(EMAIL);

        inOrder.verify(rateLimiter).record(EMAIL);
        inOrder.verify(userRepository).save(any());
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private User pendingUser() {
        return User.reconstitute(
                UserId.generate(),
                FullName.of("Maria Silva"),
                Email.of(EMAIL),
                Password.fromHash("$2a$12$hashedpassword"),
                UserStatus.PENDING_VERIFICATION,
                Set.of(UserRole.ROLE_CUSTOMER),
                new VerificationToken(UUID.randomUUID(), Instant.now().minus(1, ChronoUnit.HOURS)),
                Instant.now()
        );
    }

    private User activeUser() {
        return User.reconstitute(
                UserId.generate(),
                FullName.of("Maria Silva"),
                Email.of(EMAIL),
                Password.fromHash("$2a$12$hashedpassword"),
                UserStatus.ACTIVE,
                Set.of(UserRole.ROLE_CUSTOMER),
                new VerificationToken(UUID.randomUUID(), Instant.now().plus(24, ChronoUnit.HOURS)),
                Instant.now()
        );
    }
}
