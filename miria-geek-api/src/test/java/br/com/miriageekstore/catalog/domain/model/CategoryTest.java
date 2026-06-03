package br.com.miriageekstore.catalog.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTest {

    @Test
    void shouldCreateCategoryWithActiveStatusByDefault() {
        var category = Category.create("Action Figures", "Bonecos e miniaturas colecionáveis");

        assertThat(category.isActive()).isTrue();
        assertThat(category.getId()).isNotNull();
        assertThat(category.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldGenerateSlugOnCreation() {
        var category = Category.create("Action Figures", "Bonecos colecionáveis");

        assertThat(category.getSlug().value()).isEqualTo("action-figures");
    }

    @Test
    void shouldRegenerateSlugWhenNameChanges() {
        var category = Category.create("Action Figures", "Bonecos");

        category.update("Mangá e Anime", "Mangás e animes importados", true);

        assertThat(category.getName()).isEqualTo("Mangá e Anime");
        assertThat(category.getSlug().value()).isEqualTo("manga-e-anime");
    }

    @Test
    void shouldUpdateDescriptionAndActiveStatus() {
        var category = Category.create("Anime", "Descrição antiga");

        category.update("Anime", "Nova descrição", false);

        assertThat(category.getDescription()).isEqualTo("Nova descrição");
        assertThat(category.isActive()).isFalse();
    }
}
