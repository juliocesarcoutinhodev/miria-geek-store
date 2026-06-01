package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.exception.UserNotFoundException;
import br.com.miriageekstore.identity.domain.model.Email;
import br.com.miriageekstore.identity.domain.model.FullName;
import br.com.miriageekstore.identity.domain.model.Password;
import br.com.miriageekstore.identity.domain.model.User;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.model.UserRole;
import br.com.miriageekstore.identity.domain.model.UserStatus;
import br.com.miriageekstore.identity.domain.port.in.UpdateProfileCommand;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateProfileUseCaseTest {

    @Mock UserRepository userRepository;

    @InjectMocks UpdateProfileUseCaseImpl useCase;

    @Test
    void shouldUpdateNameAndReturnUpdatedProfile() {
        var userId = UserId.generate();
        var user = activeUser(userId, "Nome Antigo");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = useCase.execute(userId, new UpdateProfileCommand("Novo Nome Completo"));

        assertThat(result.fullName()).isEqualTo("Novo Nome Completo");
        assertThat(result.email()).isEqualTo("user@email.com");
        assertThat(result.id()).isEqualTo(userId.value());
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        var userId = UserId.generate();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(userId, new UpdateProfileCommand("Novo Nome")))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldThrowWhenNameViolatesPolicy() {
        var userId = UserId.generate();
        var user = activeUser(userId, "Nome Original");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // FullName.of() lança IllegalArgumentException para nomes < 3 chars
        assertThatThrownBy(() -> useCase.execute(userId, new UpdateProfileCommand("AB")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldNotChangeEmail() {
        var userId = UserId.generate();
        var user = activeUser(userId, "Nome Original");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = useCase.execute(userId, new UpdateProfileCommand("Nome Atualizado"));

        assertThat(result.email()).isEqualTo("user@email.com");
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private User activeUser(UserId userId, String name) {
        return User.reconstitute(
                userId, FullName.of(name), Email.of("user@email.com"),
                Password.fromHash("$2a$12$hashed"), UserStatus.ACTIVE,
                Set.of(UserRole.ROLE_CUSTOMER), null, Instant.now());
    }
}
