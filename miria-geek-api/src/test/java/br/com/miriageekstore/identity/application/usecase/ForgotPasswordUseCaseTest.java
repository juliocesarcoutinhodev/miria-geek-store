package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.event.PasswordResetRequested;
import br.com.miriageekstore.identity.domain.model.Email;
import br.com.miriageekstore.identity.domain.model.FullName;
import br.com.miriageekstore.identity.domain.model.Password;
import br.com.miriageekstore.identity.domain.model.PasswordResetToken;
import br.com.miriageekstore.identity.domain.model.User;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.model.UserRole;
import br.com.miriageekstore.identity.domain.model.UserStatus;
import br.com.miriageekstore.identity.domain.port.in.ForgotPasswordCommand;
import br.com.miriageekstore.identity.domain.port.out.DomainEventPublisher;
import br.com.miriageekstore.identity.domain.port.out.EmailSender;
import br.com.miriageekstore.identity.domain.port.out.PasswordResetTokenRepository;
import br.com.miriageekstore.identity.domain.port.out.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordUseCaseTest {

    @Mock UserRepository userRepository;
    @Mock PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock DomainEventPublisher eventPublisher;
    @Mock EmailSender emailSender;

    @InjectMocks ForgotPasswordUseCaseImpl useCase;

    private static final ForgotPasswordCommand CMD = new ForgotPasswordCommand("user@email.com");

    @Test
    void shouldGenerateTokenAndSendEmailWhenUserExists() {
        var user = activeUser();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        useCase.execute(CMD);

        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
        verify(eventPublisher).publish(any(PasswordResetRequested.class));
        verify(emailSender).sendPasswordResetEmail(any(), any(), any());
    }

    @Test
    void shouldSilentlySucceedWhenEmailNotFound() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        useCase.execute(CMD);

        verify(passwordResetTokenRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any());
        verify(emailSender, never()).sendPasswordResetEmail(any(), any(), any());
    }

    @Test
    void shouldSaveTokenBeforePublishingEvent() {
        var user = activeUser();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var inOrder = org.mockito.Mockito.inOrder(passwordResetTokenRepository, eventPublisher);
        useCase.execute(CMD);
        inOrder.verify(passwordResetTokenRepository).save(any());
        inOrder.verify(eventPublisher).publish(any());
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private User activeUser() {
        return User.reconstitute(
                UserId.generate(), FullName.of("User Test"), Email.of("user@email.com"),
                Password.fromHash("$2a$12$hashed"), UserStatus.ACTIVE,
                Set.of(UserRole.ROLE_CUSTOMER), null, Instant.now());
    }
}
