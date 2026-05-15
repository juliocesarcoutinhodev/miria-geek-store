package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.event.UserRegistered;
import br.com.miriageekstore.identity.domain.exception.EmailAlreadyExistsException;
import br.com.miriageekstore.identity.domain.exception.InvalidPasswordPolicyException;
import br.com.miriageekstore.identity.domain.exception.PasswordConfirmationException;
import br.com.miriageekstore.identity.domain.model.Email;
import br.com.miriageekstore.identity.domain.model.User;
import br.com.miriageekstore.identity.domain.model.UserStatus;
import br.com.miriageekstore.identity.domain.port.in.RegisterUserCommand;
import br.com.miriageekstore.identity.domain.port.out.DomainEventPublisher;
import br.com.miriageekstore.identity.domain.port.out.PasswordHasher;
import br.com.miriageekstore.identity.domain.port.out.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock UserRepository userRepository;
    @Mock PasswordHasher passwordHasher;
    @Mock DomainEventPublisher eventPublisher;

    RegisterUserUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegisterUserUseCaseImpl(userRepository, passwordHasher, eventPublisher);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        when(passwordHasher.hash(any())).thenReturn("$2a$12$hashed");
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var command = new RegisterUserCommand("Maria Geek", "maria@geek.com", "senha123", "senha123");
        var result = useCase.execute(command);

        assertThat(result.email()).isEqualTo("maria@geek.com");
        assertThat(result.fullName()).isEqualTo("Maria Geek");
        assertThat(result.status()).isEqualTo(UserStatus.PENDING_VERIFICATION.name());
        assertThat(result.id()).isNotNull();
        assertThat(result.createdAt()).isNotNull();
    }

    @Test
    void shouldPublishUserRegisteredEvent() {
        when(passwordHasher.hash(any())).thenReturn("$2a$12$hashed");
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(new RegisterUserCommand("Maria Geek", "maria@geek.com", "senha123", "senha123"));

        var captor = ArgumentCaptor.forClass(UserRegistered.class);
        verify(eventPublisher).publish(captor.capture());

        var event = captor.getValue();
        assertThat(event.email()).isEqualTo("maria@geek.com");
        assertThat(event.fullName()).isEqualTo("Maria Geek");
        assertThat(event.userId()).isNotNull();
        assertThat(event.verificationToken()).isNotNull();
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(any())).thenReturn(true);

        var command = new RegisterUserCommand("Maria Geek", "maria@geek.com", "senha123", "senha123");

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("maria@geek.com");

        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void shouldThrowWhenPasswordConfirmationDoesNotMatch() {
        var command = new RegisterUserCommand("Maria Geek", "maria@geek.com", "senha123", "diferente");

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(PasswordConfirmationException.class);

        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenPasswordViolatesPolicy() {
        var command = new RegisterUserCommand("Maria Geek", "maria@geek.com", "fraca", "fraca");

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(InvalidPasswordPolicyException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldNormalizeEmailToLowercase() {
        when(passwordHasher.hash(any())).thenReturn("$2a$12$hashed");
        when(userRepository.existsByEmail(Email.of("MARIA@GEEK.COM"))).thenReturn(false);
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(
                new RegisterUserCommand("Maria Geek", "MARIA@GEEK.COM", "senha123", "senha123"));

        assertThat(result.email()).isEqualTo("maria@geek.com");
    }

    @Test
    void shouldSaveUserBeforePublishingEvent() {
        when(passwordHasher.hash(any())).thenReturn("$2a$12$hashed");
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(new RegisterUserCommand("Maria Geek", "maria@geek.com", "senha123", "senha123"));

        var orderVerifier = org.mockito.Mockito.inOrder(userRepository, eventPublisher);
        orderVerifier.verify(userRepository).save(any(User.class));
        orderVerifier.verify(eventPublisher).publish(any(UserRegistered.class));
    }
}
