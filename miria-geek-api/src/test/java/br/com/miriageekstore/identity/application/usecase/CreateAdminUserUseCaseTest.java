package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.event.AdminUserCreated;
import br.com.miriageekstore.identity.domain.exception.EmailAlreadyExistsException;
import br.com.miriageekstore.identity.domain.model.Email;
import br.com.miriageekstore.identity.domain.model.User;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.model.UserRole;
import br.com.miriageekstore.identity.domain.model.UserStatus;
import br.com.miriageekstore.identity.domain.port.in.CreateAdminUserCommand;
import br.com.miriageekstore.identity.domain.port.out.DomainEventPublisher;
import br.com.miriageekstore.identity.domain.port.out.EmailSender;
import br.com.miriageekstore.identity.domain.port.out.PasswordHasher;
import br.com.miriageekstore.identity.domain.port.out.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateAdminUserUseCaseTest {

    @Mock UserRepository userRepository;
    @Mock PasswordHasher passwordHasher;
    @Mock DomainEventPublisher eventPublisher;
    @Mock EmailSender emailSender;

    @InjectMocks CreateAdminUserUseCaseImpl useCase;

    private static final UserId CREATOR_ID = UserId.generate();
    private static final CreateAdminUserCommand CMD =
            new CreateAdminUserCommand("Carlos Admin", "carlos@miriageek.com");

    @Test
    void shouldCreateAdminWithActiveStatusAndRoleAdmin() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordHasher.hash(anyString())).thenReturn("$2a$12$hashed");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(CREATOR_ID, CMD);

        assertThat(result.status()).isEqualTo("ACTIVE");
        assertThat(result.role()).isEqualTo("ROLE_ADMIN");
        assertThat(result.email()).isEqualTo("carlos@miriageek.com");
        assertThat(result.createdByAdminId()).isEqualTo(CREATOR_ID.value());
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(Email.of(CMD.email()))).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(CREATOR_ID, CMD))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
        verify(emailSender, never()).sendAdminWelcomeEmail(anyString(), anyString(), anyString());
    }

    @Test
    void shouldSendWelcomeEmailWithTempPassword() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordHasher.hash(anyString())).thenReturn("$2a$12$hashed");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(CREATOR_ID, CMD);

        verify(emailSender).sendAdminWelcomeEmail(
                anyString(), anyString(), anyString());
    }

    @Test
    void shouldPublishAdminUserCreatedEvent() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordHasher.hash(anyString())).thenReturn("$2a$12$hashed");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(CREATOR_ID, CMD);

        verify(eventPublisher).publish(any(AdminUserCreated.class));
    }

    @Test
    void shouldNotRequireEmailVerification() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordHasher.hash(anyString())).thenReturn("$2a$12$hashed");
        when(userRepository.save(any())).thenAnswer(inv -> {
            User saved = inv.getArgument(0);
            // admin accounts have no verification token
            assertThat(saved.getVerificationToken()).isNull();
            assertThat(saved.getStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(saved.getRoles()).containsExactly(UserRole.ROLE_ADMIN);
            return saved;
        });

        useCase.execute(CREATOR_ID, CMD);
    }
}
