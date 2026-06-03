package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.InsufficientStockException;
import br.com.miriageekstore.catalog.domain.exception.ProductNotFoundException;
import br.com.miriageekstore.catalog.domain.model.CategoryId;
import br.com.miriageekstore.catalog.domain.model.Product;
import br.com.miriageekstore.catalog.domain.model.ProductVariant;
import br.com.miriageekstore.catalog.domain.model.Sku;
import br.com.miriageekstore.catalog.domain.model.StockMovementType;
import br.com.miriageekstore.catalog.domain.port.in.AdjustStockCommand;
import br.com.miriageekstore.catalog.domain.port.out.CatalogEventPublisher;
import br.com.miriageekstore.catalog.domain.port.out.ProductRepository;
import br.com.miriageekstore.catalog.domain.port.out.StockMovementRepository;
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
class AdjustStockUseCaseTest {

    @Mock ProductRepository productRepository;
    @Mock StockMovementRepository stockMovementRepository;
    @Mock CatalogEventPublisher eventPublisher;
    @InjectMocks AdjustStockUseCaseImpl useCase;

    @Test
    void shouldIncreaseStockOnEntrada() {
        var product = product(5);
        var variantId = product.getVariants().get(0).getId().value();
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(command(product.getId().value(), variantId, StockMovementType.ENTRADA, 10));

        assertThat(result.stock()).isEqualTo(15);
        verify(stockMovementRepository).save(any());
        verify(eventPublisher).publish(any());
    }

    @Test
    void shouldDecreaseStockOnSaida() {
        var product = product(10);
        var variantId = product.getVariants().get(0).getId().value();
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(command(product.getId().value(), variantId, StockMovementType.SAIDA, 4));

        assertThat(result.stock()).isEqualTo(6);
        verify(stockMovementRepository).save(any());
        verify(eventPublisher).publish(any());
    }

    @Test
    void shouldThrowWhenStockWouldGoNegative() {
        var product = product(3);
        var variantId = product.getVariants().get(0).getId().value();
        when(productRepository.findById(any())).thenReturn(Optional.of(product));

        assertThatThrownBy(() ->
                useCase.execute(command(product.getId().value(), variantId, StockMovementType.SAIDA, 5)))
                .isInstanceOf(InsufficientStockException.class);

        verify(productRepository, never()).save(any());
        verify(stockMovementRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void shouldThrowWhenProductNotFound() {
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                useCase.execute(command(UUID.randomUUID(), UUID.randomUUID(), StockMovementType.ENTRADA, 1)))
                .isInstanceOf(ProductNotFoundException.class);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Product product(int initialStock) {
        var v = ProductVariant.create("Cor", "Preto", BigDecimal.valueOf(99), initialStock, new Sku("SKU-001"));
        return Product.create("Funko Batman", "Desc", CategoryId.of(UUID.randomUUID()), false, List.of(v));
    }

    private AdjustStockCommand command(UUID productId, UUID variantId, StockMovementType type, int quantity) {
        return new AdjustStockCommand(productId, variantId, type, quantity, "Ajuste de teste", UUID.randomUUID());
    }
}
