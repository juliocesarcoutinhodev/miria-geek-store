package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.CategoryNotFoundException;
import br.com.miriageekstore.catalog.domain.exception.ProductSkuAlreadyExistsException;
import br.com.miriageekstore.catalog.domain.model.Category;
import br.com.miriageekstore.catalog.domain.model.CategoryId;
import br.com.miriageekstore.catalog.domain.model.Product;
import br.com.miriageekstore.catalog.domain.port.in.CreateProductCommand;
import br.com.miriageekstore.catalog.domain.port.out.CategoryRepository;
import br.com.miriageekstore.catalog.domain.port.out.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateProductUseCaseTest {

    @Mock ProductRepository productRepository;
    @Mock CategoryRepository categoryRepository;
    @InjectMocks CreateProductUseCaseImpl useCase;

    @Test
    void shouldCreateProductSuccessfully() {
        var category = Category.create("Action Figures", "Desc");
        var cmd = command(category.getId().value(), null);

        when(categoryRepository.findById(any())).thenReturn(Optional.of(category));
        when(productRepository.existsBySku(anyString())).thenReturn(false);
        when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(cmd);

        assertThat(result.name()).isEqualTo("Funko Pop Batman");
        assertThat(result.slug()).isEqualTo("funko-pop-batman");
        assertThat(result.status()).isEqualTo("INACTIVE");
        assertThat(result.featured()).isFalse();
        assertThat(result.variants()).hasSize(1);
        assertThat(result.variants().get(0).sku()).isNotBlank();
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldUseCustomSkuWhenProvided() {
        var category = Category.create("Action Figures", "Desc");
        var cmd = command(category.getId().value(), "CUSTOM-SKU-001");

        when(categoryRepository.findById(any())).thenReturn(Optional.of(category));
        when(productRepository.existsBySku("CUSTOM-SKU-001")).thenReturn(false);
        when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(cmd);

        assertThat(result.variants().get(0).sku()).isEqualTo("CUSTOM-SKU-001");
    }

    @Test
    void shouldThrowWhenCategoryNotFound() {
        var cmd = command(UUID.randomUUID(), null);
        when(categoryRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(cmd))
                .isInstanceOf(CategoryNotFoundException.class);

        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenSkuAlreadyExists() {
        var category = Category.create("Action Figures", "Desc");
        var cmd = command(category.getId().value(), "EXISTING-SKU");

        when(categoryRepository.findById(any())).thenReturn(Optional.of(category));
        when(productRepository.existsBySku("EXISTING-SKU")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(cmd))
                .isInstanceOf(ProductSkuAlreadyExistsException.class)
                .hasMessageContaining("EXISTING-SKU");

        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldAutoGenerateSkuWhenNotProvided() {
        var category = Category.create("Action Figures", "Desc");
        var cmd = command(category.getId().value(), null);

        when(categoryRepository.findById(any())).thenReturn(Optional.of(category));
        when(productRepository.existsBySku(anyString())).thenReturn(false);
        when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(cmd);

        assertThat(result.variants().get(0).sku())
                .startsWith("FUNKO-POP-BATMAN");
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private CreateProductCommand command(UUID categoryId, String sku) {
        var variant = new CreateProductCommand.VariantInput("Cor", "Preto",
                BigDecimal.valueOf(89.90), 10, sku);
        return new CreateProductCommand("Funko Pop Batman", "Boneco colecionável Batman",
                categoryId, false, List.of(variant));
    }
}
