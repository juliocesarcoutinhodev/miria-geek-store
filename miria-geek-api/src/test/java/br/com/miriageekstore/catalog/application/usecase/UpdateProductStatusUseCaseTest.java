package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.ProductCannotBeActivatedException;
import br.com.miriageekstore.catalog.domain.exception.ProductNotFoundException;
import br.com.miriageekstore.catalog.domain.model.Category;
import br.com.miriageekstore.catalog.domain.model.CategoryId;
import br.com.miriageekstore.catalog.domain.model.Product;
import br.com.miriageekstore.catalog.domain.model.ProductId;
import br.com.miriageekstore.catalog.domain.model.ProductStatus;
import br.com.miriageekstore.catalog.domain.model.Sku;
import br.com.miriageekstore.catalog.domain.model.Slug;
import br.com.miriageekstore.catalog.domain.model.ProductVariant;
import br.com.miriageekstore.catalog.domain.port.in.UpdateProductStatusCommand;
import br.com.miriageekstore.catalog.domain.port.out.CatalogEventPublisher;
import br.com.miriageekstore.catalog.domain.port.out.CategoryRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateProductStatusUseCaseTest {

    @Mock ProductRepository productRepository;
    @Mock ProductImageRepository imageRepository;
    @Mock CategoryRepository categoryRepository;
    @Mock CatalogEventPublisher eventPublisher;
    @InjectMocks UpdateProductStatusUseCaseImpl useCase;

    @Test
    void shouldActivateProductWhenAllConditionsMet() {
        var category = Category.create("Action Figures", "Desc");
        var product = product(category.getId());

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(imageRepository.countByProductId(product.getId())).thenReturn(2L);
        when(categoryRepository.findById(product.getCategoryId())).thenReturn(Optional.of(category));
        when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(new UpdateProductStatusCommand(product.getId(), ProductStatus.ACTIVE));

        assertThat(result.status()).isEqualTo("ACTIVE");
        verify(eventPublisher).publish(any());
    }

    @Test
    void shouldDeactivateProductWithoutRestrictions() {
        var category = Category.create("Cat", "Desc");
        var product = product(category.getId());

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(new UpdateProductStatusCommand(product.getId(), ProductStatus.INACTIVE));

        assertThat(result.status()).isEqualTo("INACTIVE");
        verify(imageRepository, never()).countByProductId(any());
    }

    @Test
    void shouldThrowWhenActivatingWithNoImages() {
        var category = Category.create("Cat", "Desc");
        var product = product(category.getId());

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(imageRepository.countByProductId(product.getId())).thenReturn(0L);
        when(categoryRepository.findById(product.getCategoryId())).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> useCase.execute(
                new UpdateProductStatusCommand(product.getId(), ProductStatus.ACTIVE)))
                .isInstanceOf(ProductCannotBeActivatedException.class)
                .hasMessageContaining("imagens");

        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenActivatingWithInactiveCategory() {
        var category = Category.create("Cat", "Desc");
        category.update("Cat", "Desc", false);
        var product = product(category.getId());

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(imageRepository.countByProductId(product.getId())).thenReturn(1L);
        when(categoryRepository.findById(product.getCategoryId())).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> useCase.execute(
                new UpdateProductStatusCommand(product.getId(), ProductStatus.ACTIVE)))
                .isInstanceOf(ProductCannotBeActivatedException.class)
                .hasMessageContaining("categoria");

        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenProductNotFound() {
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(
                new UpdateProductStatusCommand(ProductId.generate(), ProductStatus.ACTIVE)))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void shouldThrowWithAllReasonsWhenMultipleConditionsFail() {
        var category = Category.create("Cat", "Desc");
        category.update("Cat", "Desc", false);
        var product = product(category.getId());

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(imageRepository.countByProductId(product.getId())).thenReturn(0L);
        when(categoryRepository.findById(product.getCategoryId())).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> useCase.execute(
                new UpdateProductStatusCommand(product.getId(), ProductStatus.ACTIVE)))
                .isInstanceOf(ProductCannotBeActivatedException.class)
                .hasMessageContaining("imagens")
                .hasMessageContaining("categoria");
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Product product(CategoryId categoryId) {
        var slug = Slug.from("Produto Teste");
        var sku = Sku.generate(slug, "Cor", "Azul");
        return Product.create("Produto Teste", "Desc", categoryId, false,
                List.of(ProductVariant.create("Cor", "Azul", BigDecimal.TEN, 5, sku,
                        BigDecimal.valueOf(0.350), BigDecimal.valueOf(15), BigDecimal.valueOf(10), BigDecimal.valueOf(20))));
    }
}
