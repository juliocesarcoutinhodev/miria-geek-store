package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.exception.UserNotFoundException;
import br.com.miriageekstore.identity.domain.model.Email;
import br.com.miriageekstore.identity.domain.model.FullName;
import br.com.miriageekstore.identity.domain.model.Password;
import br.com.miriageekstore.identity.domain.model.User;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.model.UserRole;
import br.com.miriageekstore.identity.domain.model.UserStatus;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProfileUseCaseTest {

    @Mock UserRepository userRepository;

    @InjectMocks GetProfileUseCaseImpl useCase;

    @Test
    void shouldReturnProfileWithoutSensitiveData() {
        var userId = UserId.generate();
        var user = activeUser(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        var result = useCase.execute(userId);

        assertThat(result.id()).isEqualTo(userId.value());
        assertThat(result.fullName()).isEqualTo("Maria Silva");
        assertThat(result.email()).isEqualTo("maria@email.com");
        assertThat(result.status()).isEqualTo("ACTIVE");
        assertThat(result.roles()).containsExactly("ROLE_CUSTOMER");
        assertThat(result.createdAt()).isNotNull();
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        var userId = UserId.generate();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(userId))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldNotExposePasswordInResult() {
        var userId = UserId.generate();
        var user = activeUser(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        var result = useCase.execute(userId);

        // ProfileResult has no password field — compile-time guarantee
        // Just check no hash value leaks through any field
        assertThat(result.toString()).doesNotContain("hashed");
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private User activeUser(UserId userId) {
        return User.reconstitute(
                userId, FullName.of("Maria Silva"), Email.of("maria@email.com"),
                Password.fromHash("$2a$12$hashed"), UserStatus.ACTIVE,
                Set.of(UserRole.ROLE_CUSTOMER), null, Instant.now(), null, null);
    }
}
