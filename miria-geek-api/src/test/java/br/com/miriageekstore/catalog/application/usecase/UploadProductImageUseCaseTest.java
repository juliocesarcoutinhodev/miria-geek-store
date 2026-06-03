package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.ImageLimitExceededException;
import br.com.miriageekstore.catalog.domain.exception.InvalidImageTypeException;
import br.com.miriageekstore.catalog.domain.exception.ProductNotFoundException;
import br.com.miriageekstore.catalog.domain.model.Category;
import br.com.miriageekstore.catalog.domain.model.Product;
import br.com.miriageekstore.catalog.domain.model.ProductId;
import br.com.miriageekstore.catalog.domain.model.ProductImage;
import br.com.miriageekstore.catalog.domain.port.in.UploadProductImageCommand;
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

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UploadProductImageUseCaseTest {

    @Mock ProductRepository productRepository;
    @Mock ProductImageRepository imageRepository;
    @Mock StoragePort storagePort;
    @InjectMocks UploadProductImageUseCaseImpl useCase;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(useCase, "bucket", "miria-products");
    }

    @Test
    void shouldUploadFirstImageAsPrincipal() {
        var product = product();
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(imageRepository.countByProductId(any())).thenReturn(0L);
        when(storagePort.uploadFile(anyString(), anyString(), any(), anyString()))
                .thenReturn("http://localhost:9000/miria-products/img.jpg");
        when(imageRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var command = new UploadProductImageCommand(
                product.getId().value(), InputStream.nullInputStream(), "image/jpeg", 1024L);
        var result = useCase.execute(command);

        assertThat(result.principal()).isTrue();
        assertThat(result.imageOrder()).isZero();
        assertThat(result.url()).contains("miria-products");
        verify(storagePort).uploadFile(anyString(), anyString(), any(), anyString());
    }

    @Test
    void shouldUploadSubsequentImageAsNonPrincipal() {
        var product = product();
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(imageRepository.countByProductId(any())).thenReturn(3L);
        when(storagePort.uploadFile(anyString(), anyString(), any(), anyString()))
                .thenReturn("http://localhost:9000/miria-products/img2.jpg");
        when(imageRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var command = new UploadProductImageCommand(
                product.getId().value(), InputStream.nullInputStream(), "image/png", 2048L);
        var result = useCase.execute(command);

        assertThat(result.principal()).isFalse();
        assertThat(result.imageOrder()).isEqualTo(3);
    }

    @Test
    void shouldThrowForInvalidContentType() {
        var command = new UploadProductImageCommand(
                ProductId.generate().value(), InputStream.nullInputStream(), "image/gif", 1024L);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(InvalidImageTypeException.class)
                .hasMessageContaining("image/gif");

        verify(productRepository, never()).findById(any());
    }

    @Test
    void shouldThrowWhenProductNotFound() {
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        var command = new UploadProductImageCommand(
                ProductId.generate().value(), InputStream.nullInputStream(), "image/jpeg", 1024L);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(ProductNotFoundException.class);

        verify(storagePort, never()).uploadFile(anyString(), anyString(), any(), anyString());
    }

    @Test
    void shouldThrowWhenImageLimitReached() {
        var product = product();
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(imageRepository.countByProductId(any())).thenReturn(10L);

        var command = new UploadProductImageCommand(
                product.getId().value(), InputStream.nullInputStream(), "image/jpeg", 1024L);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(ImageLimitExceededException.class);

        verify(storagePort, never()).uploadFile(anyString(), anyString(), any(), anyString());
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Product product() {
        var slug = br.com.miriageekstore.catalog.domain.model.Slug.from("Produto Teste");
        var sku = br.com.miriageekstore.catalog.domain.model.Sku.generate(slug, "Cor", "Azul");
        var variant = br.com.miriageekstore.catalog.domain.model.ProductVariant.create(
                "Cor", "Azul", BigDecimal.TEN, 5, sku);
        return Product.create("Produto Teste", "Desc",
                br.com.miriageekstore.catalog.domain.model.CategoryId.generate(), false, List.of(variant));
    }
}
