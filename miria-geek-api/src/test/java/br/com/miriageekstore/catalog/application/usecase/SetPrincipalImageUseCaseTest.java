package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.ProductImageNotFoundException;
import br.com.miriageekstore.catalog.domain.exception.ProductNotFoundException;
import br.com.miriageekstore.catalog.domain.model.ImageId;
import br.com.miriageekstore.catalog.domain.model.Product;
import br.com.miriageekstore.catalog.domain.model.ProductId;
import br.com.miriageekstore.catalog.domain.model.ProductImage;
import br.com.miriageekstore.catalog.domain.port.in.SetPrincipalImageCommand;
import br.com.miriageekstore.catalog.domain.port.out.ProductImageRepository;
import br.com.miriageekstore.catalog.domain.port.out.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SetPrincipalImageUseCaseTest {

    @Mock ProductRepository productRepository;
    @Mock ProductImageRepository imageRepository;
    @InjectMocks SetPrincipalImageUseCaseImpl useCase;

    @Test
    void shouldSetNewPrincipalAndUnsetPrevious() {
        var product = product();
        var oldPrincipal = image(product.getId(), true, 0);
        var newPrincipal = image(product.getId(), false, 1);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(imageRepository.findById(newPrincipal.getId())).thenReturn(Optional.of(newPrincipal));
        when(imageRepository.findPrincipalByProductId(product.getId())).thenReturn(Optional.of(oldPrincipal));
        when(imageRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var cmd = new SetPrincipalImageCommand(product.getId().value(), newPrincipal.getId().value());
        var result = useCase.execute(cmd);

        assertThat(result.principal()).isTrue();
        assertThat(oldPrincipal.isPrincipal()).isFalse();
        verify(imageRepository).save(oldPrincipal);
        verify(imageRepository).save(newPrincipal);
    }

    @Test
    void shouldThrowWhenProductNotFound() {
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(
                new SetPrincipalImageCommand(ProductId.generate().value(), ImageId.generate().value())))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void shouldThrowWhenImageNotFound() {
        var product = product();
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(imageRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(
                new SetPrincipalImageCommand(product.getId().value(), ImageId.generate().value())))
                .isInstanceOf(ProductImageNotFoundException.class);
    }

    @Test
    void shouldThrowWhenImageBelongsToAnotherProduct() {
        var product = product();
        var otherProductImage = image(ProductId.generate(), false, 0);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(imageRepository.findById(otherProductImage.getId())).thenReturn(Optional.of(otherProductImage));

        assertThatThrownBy(() -> useCase.execute(
                new SetPrincipalImageCommand(product.getId().value(), otherProductImage.getId().value())))
                .isInstanceOf(ProductImageNotFoundException.class);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Product product() {
        var slug = br.com.miriageekstore.catalog.domain.model.Slug.from("Produto");
        var sku = br.com.miriageekstore.catalog.domain.model.Sku.generate(slug, "Cor", "Azul");
        var variant = br.com.miriageekstore.catalog.domain.model.ProductVariant.create(
                "Cor", "Azul", BigDecimal.TEN, 5, sku);
        return Product.create("Produto", "Desc",
                br.com.miriageekstore.catalog.domain.model.CategoryId.generate(), false, List.of(variant));
    }

    private ProductImage image(ProductId productId, boolean principal, int order) {
        return ProductImage.create(productId, "http://example.com/img.jpg",
                "file.jpg", principal, order);
    }
}
