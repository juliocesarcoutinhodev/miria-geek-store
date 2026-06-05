package br.com.miriageekstore.catalog.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    void shouldCreateProductWithInactiveStatusByDefault() {
        var product = product();

        assertThat(product.getStatus()).isEqualTo(ProductStatus.INACTIVE);
        assertThat(product.getId()).isNotNull();
        assertThat(product.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldGenerateSlugOnCreation() {
        var product = product();

        assertThat(product.getSlug().value()).isEqualTo("funko-pop-batman");
    }

    @Test
    void shouldCreateWithFeaturedFalseByDefault() {
        var product = Product.create("Funko Pop Batman", "Desc",
                CategoryId.generate(), false, List.of(variant()));

        assertThat(product.isFeatured()).isFalse();
    }

    @Test
    void shouldCreateWithFeaturedTrue() {
        var product = Product.create("Funko Pop Batman", "Desc",
                CategoryId.generate(), true, List.of(variant()));

        assertThat(product.isFeatured()).isTrue();
    }

    @Test
    void shouldThrowWhenCreatedWithNoVariants() {
        assertThatThrownBy(() ->
                Product.create("Produto", "Desc", CategoryId.generate(), false, List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldHaveVariantsAfterCreation() {
        var product = product();

        assertThat(product.getVariants()).hasSize(1);
        assertThat(product.getVariants().get(0).getSku().value()).isEqualTo("FUNKO-POP-BATMAN-COR-PRETO");
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Product product() {
        return Product.create("Funko Pop Batman", "Boneco colecionável",
                CategoryId.generate(), false, List.of(variant()));
    }

    private ProductVariant variant() {
        var slug = Slug.from("Funko Pop Batman");
        return ProductVariant.create("Cor", "Preto", BigDecimal.valueOf(89.90), 10,
                Sku.generate(slug, "Cor", "Preto"),
                BigDecimal.valueOf(0.350), BigDecimal.valueOf(15), BigDecimal.valueOf(10), BigDecimal.valueOf(20));
    }
}
