package br.com.miriageekstore.catalog.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SlugTest {

    @Test
    void shouldGenerateSlugFromSimpleName() {
        assertThat(Slug.from("Action Figures").value()).isEqualTo("action-figures");
    }

    @Test
    void shouldLowercaseName() {
        assertThat(Slug.from("ANIME").value()).isEqualTo("anime");
    }

    @Test
    void shouldReplaceMultipleSpacesWithSingleHyphen() {
        assertThat(Slug.from("Manga  e  Anime").value()).isEqualTo("manga-e-anime");
    }

    @Test
    void shouldNormalizeAccentedCharacters() {
        assertThat(Slug.from("Ação").value()).isEqualTo("acao");
        assertThat(Slug.from("Coleções Geek").value()).isEqualTo("colecoes-geek");
    }

    @Test
    void shouldRemoveSpecialCharacters() {
        assertThat(Slug.from("Games & Consoles!").value()).isEqualTo("games-consoles");
    }

    @Test
    void shouldCollapseMultipleHyphens() {
        assertThat(Slug.from("Geek---Store").value()).isEqualTo("geek-store");
    }

    @Test
    void shouldTrimLeadingAndTrailingSpaces() {
        assertThat(Slug.from("  Anime  ").value()).isEqualTo("anime");
    }
}
