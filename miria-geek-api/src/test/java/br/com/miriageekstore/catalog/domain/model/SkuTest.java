package br.com.miriageekstore.catalog.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SkuTest {

    @Test
    void shouldGenerateSkuFromSlugAndAttributes() {
        var slug = Slug.from("Funko Pop Batman");
        var sku = Sku.generate(slug, "Cor", "Azul");

        assertThat(sku.value()).isEqualTo("FUNKO-POP-BATMAN-COR-AZUL");
    }

    @Test
    void shouldNormalizeAccentsInGeneratedSku() {
        var slug = Slug.from("Camiseta Anime");
        var sku = Sku.generate(slug, "Tamanho", "Médio");

        assertThat(sku.value()).isEqualTo("CAMISETA-ANIME-TAMANHO-MEDIO");
    }

    @Test
    void shouldCollapseMultipleHyphensInGeneratedSku() {
        var slug = Slug.from("Produto Teste");
        var sku = Sku.generate(slug, "Cor & Tamanho", "P");

        assertThat(sku.value()).doesNotContain("--");
    }

    @Test
    void shouldThrowWhenValueIsBlank() {
        assertThatThrownBy(() -> new Sku(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowWhenValueIsNull() {
        assertThatThrownBy(() -> new Sku(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
