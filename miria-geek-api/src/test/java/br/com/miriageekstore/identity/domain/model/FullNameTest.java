package br.com.miriageekstore.identity.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FullNameTest {

    @Test
    void shouldCreateValidFullName() {
        var name = FullName.of("João Silva");
        assertThat(name.value()).isEqualTo("João Silva");
    }

    @Test
    void shouldStripWhitespace() {
        var name = FullName.of("  Maria Oliveira  ");
        assertThat(name.value()).isEqualTo("Maria Oliveira");
    }

    @Test
    void shouldRejectBlankName() {
        assertThatThrownBy(() -> FullName.of("   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectNull() {
        assertThatThrownBy(() -> FullName.of(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectTooShortName() {
        assertThatThrownBy(() -> FullName.of("AB"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("3");
    }

    @Test
    void shouldRejectNameExceeding255Chars() {
        var longName = "A".repeat(256);
        assertThatThrownBy(() -> FullName.of(longName))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
