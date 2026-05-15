package br.com.miriageekstore.identity.domain.model;

import br.com.miriageekstore.identity.domain.exception.InvalidEmailException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @Test
    void shouldCreateValidEmail() {
        var email = Email.of("user@example.com");
        assertThat(email.value()).isEqualTo("user@example.com");
    }

    @Test
    void shouldNormalizeToLowercase() {
        var email = Email.of("User@EXAMPLE.COM");
        assertThat(email.value()).isEqualTo("user@example.com");
    }

    @Test
    void shouldStripWhitespace() {
        var email = Email.of("  user@example.com  ");
        assertThat(email.value()).isEqualTo("user@example.com");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "notanemail", "missing@", "@nodomain.com", "no-at-sign"})
    void shouldRejectInvalidFormat(String invalid) {
        assertThatThrownBy(() -> Email.of(invalid))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void shouldRejectNull() {
        assertThatThrownBy(() -> Email.of(null))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void shouldBeEqualForSameValue() {
        assertThat(Email.of("a@b.com")).isEqualTo(Email.of("A@B.COM"));
    }
}
