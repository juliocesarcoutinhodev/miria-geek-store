package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.CannotRemovePrincipalImageException;
import br.com.miriageekstore.catalog.domain.exception.ProductImageNotFoundException;
import br.com.miriageekstore.catalog.domain.exception.ProductNotFoundException;
import br.com.miriageekstore.catalog.domain.model.ImageId;
import br.com.miriageekstore.catalog.domain.model.Product;
import br.com.miriageekstore.catalog.domain.model.ProductId;
import br.com.miriageekstore.catalog.domain.model.ProductImage;
import br.com.miriageekstore.catalog.domain.port.in.DeleteProductImageCommand;
import br.com.miriageekstore.catalog.domain.port.out.ProductImageRepository;
import br.com.miriageekstore.catalog.domain.port.out.ProductRepository;
import br.com.miriageekstore.catalog.domain.port.out.StoragePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteProductImageUseCaseTest {

    @Mock ProductRepository productRepository;
    @Mock ProductImageRepository imageRepository;
    @Mock StoragePort storagePort;
    @InjectMocks DeleteProductImageUseCaseImpl useCase;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(useCase, "bucket", "miria-products");
    }

    @Test
    void shouldDeleteNonPrincipalImageSuccessfully() {
        var product = product();
        var image = image(product.getId(), false);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(imageRepository.findById(image.getId())).thenReturn(Optional.of(image));

        assertThatCode(() -> useCase.execute(
                new DeleteProductImageCommand(product.getId().value(), image.getId().value())))
                .doesNotThrowAnyException();

        verify(storagePort).deleteFile("miria-products", image.getFilename());
        verify(imageRepository).deleteById(image.getId());
    }

    @Test
    void shouldDeletePrincipalImageWhenItIsTheOnlyOne() {
        var product = product();
        var image = image(product.getId(), true);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(imageRepository.findById(image.getId())).thenReturn(Optional.of(image));
        when(imageRepository.countByProductId(product.getId())).thenReturn(1L);

        assertThatCode(() -> useCase.execute(
                new DeleteProductImageCommand(product.getId().value(), image.getId().value())))
                .doesNotThrowAnyException();

        verify(storagePort).deleteFile(anyString(), anyString());
        verify(imageRepository).deleteById(image.getId());
    }

    @Test
    void shouldThrowWhenRemovingPrincipalWithOthersPresent() {
        var product = product();
        var image = image(product.getId(), true);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(imageRepository.findById(image.getId())).thenReturn(Optional.of(image));
        when(imageRepository.countByProductId(product.getId())).thenReturn(3L);

        assertThatThrownBy(() -> useCase.execute(
                new DeleteProductImageCommand(product.getId().value(), image.getId().value())))
                .isInstanceOf(CannotRemovePrincipalImageException.class);

        verify(storagePort, never()).deleteFile(anyString(), anyString());
        verify(imageRepository, never()).deleteById(any());
    }

    @Test
    void shouldThrowWhenProductNotFound() {
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(
                new DeleteProductImageCommand(ProductId.generate().value(), ImageId.generate().value())))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void shouldThrowWhenImageNotFound() {
        var product = product();
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(imageRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(
                new DeleteProductImageCommand(product.getId().value(), ImageId.generate().value())))
                .isInstanceOf(ProductImageNotFoundException.class);

        verify(storagePort, never()).deleteFile(anyString(), anyString());
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

    private ProductImage image(ProductId productId, boolean principal) {
        return ProductImage.create(productId, "http://example.com/img.jpg",
                "product-uuid.jpg", principal, 0);
    }
}
