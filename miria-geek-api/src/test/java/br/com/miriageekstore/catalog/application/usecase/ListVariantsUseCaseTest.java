package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.ProductNotFoundException;
import br.com.miriageekstore.catalog.domain.model.CategoryId;
import br.com.miriageekstore.catalog.domain.model.Product;
import br.com.miriageekstore.catalog.domain.model.ProductId;
import br.com.miriageekstore.catalog.domain.model.ProductVariant;
import br.com.miriageekstore.catalog.domain.model.Sku;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListVariantsUseCaseTest {

    @Mock ProductRepository productRepository;
    @InjectMocks ListVariantsUseCaseImpl useCase;

    @Test
    void shouldReturnVariantsOfProduct() {
        var product = product();
        when(productRepository.findById(any())).thenReturn(Optional.of(product));

        var result = useCase.execute(product.getId().value());

        assertThat(result.variants()).hasSize(1);
        var item = result.variants().get(0);
        assertThat(item.attributeName()).isEqualTo("Cor");
        assertThat(item.sku()).isEqualTo("SKU-001");
        assertThat(item.active()).isTrue();
    }

    @Test
    void shouldThrowWhenProductNotFound() {
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(UUID.randomUUID()))
                .isInstanceOf(ProductNotFoundException.class);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Product product() {
        var variant = ProductVariant.create("Cor", "Preto", BigDecimal.valueOf(99.90), 5, new Sku("SKU-001"));
        return Product.create("Funko Batman", "Desc", CategoryId.of(UUID.randomUUID()), false, List.of(variant));
    }
}
