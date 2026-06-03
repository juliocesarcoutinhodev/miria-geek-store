package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.ProductNotFoundException;
import br.com.miriageekstore.catalog.domain.exception.ProductSkuAlreadyExistsException;
import br.com.miriageekstore.catalog.domain.exception.VariantNotFoundException;
import br.com.miriageekstore.catalog.domain.model.CategoryId;
import br.com.miriageekstore.catalog.domain.model.Product;
import br.com.miriageekstore.catalog.domain.model.ProductVariant;
import br.com.miriageekstore.catalog.domain.model.Sku;
import br.com.miriageekstore.catalog.domain.port.in.UpdateVariantCommand;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateVariantUseCaseTest {

    @Mock ProductRepository productRepository;
    @InjectMocks UpdateVariantUseCaseImpl useCase;

    @Test
    void shouldUpdateVariantSuccessfully() {
        var product = product();
        var variantId = product.getVariants().get(0).getId().value();
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // same SKU — existsBySkuExcluding is NOT called
        var result = useCase.execute(command(product.getId().value(), variantId, "SKU-001"));

        assertThat(result.attributeName()).isEqualTo("Tamanho");
        assertThat(result.price()).isEqualByComparingTo(BigDecimal.valueOf(79.90));
        verify(productRepository).save(any());
    }

    @Test
    void shouldAllowSameSkuWithoutConflictCheck() {
        var product = product();
        var variantId = product.getVariants().get(0).getId().value();
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(command(product.getId().value(), variantId, "SKU-001"));

        assertThat(result.sku()).isEqualTo("SKU-001");
    }

    @Test
    void shouldThrowWhenNewSkuConflictsWithAnotherVariant() {
        var product = product();
        var variantId = product.getVariants().get(0).getId().value();
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(productRepository.existsBySkuExcluding("SKU-OUTRO", variantId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(command(product.getId().value(), variantId, "SKU-OUTRO")))
                .isInstanceOf(ProductSkuAlreadyExistsException.class);

        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenProductNotFound() {
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(command(UUID.randomUUID(), UUID.randomUUID(), "SKU-X")))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void shouldThrowWhenVariantNotFound() {
        var product = product();
        when(productRepository.findById(any())).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> useCase.execute(command(product.getId().value(), UUID.randomUUID(), "SKU-X")))
                .isInstanceOf(VariantNotFoundException.class);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Product product() {
        var v = ProductVariant.create("Cor", "Preto", BigDecimal.valueOf(99), 5, new Sku("SKU-001"));
        return Product.create("Funko Batman", "Desc", CategoryId.of(UUID.randomUUID()), false, List.of(v));
    }

    private UpdateVariantCommand command(UUID productId, UUID variantId, String sku) {
        return new UpdateVariantCommand(productId, variantId, "Tamanho", "P", BigDecimal.valueOf(79.90), 2, sku);
    }
}
