package br.com.miriageekstore.identity.domain.model;

import br.com.miriageekstore.identity.domain.exception.InvalidPasswordPolicyException;
import br.com.miriageekstore.identity.domain.port.out.PasswordHasher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordTest {

    @Mock
    PasswordHasher hasher;

    @Test
    void shouldHashValidPassword() {
        when(hasher.hash("secret12")).thenReturn("$2a$12$hash");

        var password = Password.hash("secret12", hasher);

        assertThat(password.hash()).isEqualTo("$2a$12$hash");
    }

    @Test
    void shouldReconstitueFromExistingHash() {
        var password = Password.fromHash("$2a$12$existingHash");
        assertThat(password.hash()).isEqualTo("$2a$12$existingHash");
    }

    @ParameterizedTest
    @ValueSource(strings = {"short1", "1234567", "abc"})
    void shouldRejectPasswordShorterThan8Chars(String raw) {
        assertThatThrownBy(() -> Password.hash(raw, hasher))
                .isInstanceOf(InvalidPasswordPolicyException.class)
                .hasMessageContaining("8");
    }

    @Test
    void shouldRejectPasswordWithoutNumbers() {
        assertThatThrownBy(() -> Password.hash("onlyletters", hasher))
                .isInstanceOf(InvalidPasswordPolicyException.class)
                .hasMessageContaining("number");
    }

    @Test
    void shouldRejectPasswordWithoutLetters() {
        assertThatThrownBy(() -> Password.hash("12345678", hasher))
                .isInstanceOf(InvalidPasswordPolicyException.class)
                .hasMessageContaining("letter");
    }

    @Test
    void shouldRejectNullPassword() {
        assertThatThrownBy(() -> Password.hash(null, hasher))
                .isInstanceOf(InvalidPasswordPolicyException.class);
    }
}
