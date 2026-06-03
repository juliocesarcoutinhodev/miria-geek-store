package br.com.miriageekstore.catalog.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductImageTest {

    @Test
    void shouldCreateImageWithGivenPrincipalFlag() {
        var productId = ProductId.generate();
        var image = ProductImage.create(productId, "http://example.com/img.jpg",
                "product-uuid.jpg", true, 0);

        assertThat(image.getId()).isNotNull();
        assertThat(image.isPrincipal()).isTrue();
        assertThat(image.getImageOrder()).isZero();
        assertThat(image.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldChangePrincipalFlag() {
        var image = ProductImage.create(ProductId.generate(), "http://example.com/img.jpg",
                "file.jpg", true, 0);

        image.setPrincipal(false);

        assertThat(image.isPrincipal()).isFalse();
    }

    @Test
    void shouldChangeImageOrder() {
        var image = ProductImage.create(ProductId.generate(), "http://example.com/img.jpg",
                "file.jpg", false, 0);

        image.setImageOrder(3);

        assertThat(image.getImageOrder()).isEqualTo(3);
    }

    @Test
    void shouldLinkImageToProduct() {
        var productId = ProductId.generate();
        var image = ProductImage.create(productId, "http://example.com/img.jpg",
                "file.jpg", false, 1);

        assertThat(image.getProductId()).isEqualTo(productId);
        assertThat(image.getUrl()).isEqualTo("http://example.com/img.jpg");
        assertThat(image.getFilename()).isEqualTo("file.jpg");
    }
}
