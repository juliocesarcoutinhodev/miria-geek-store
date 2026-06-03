package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.LastActiveVariantException;
import br.com.miriageekstore.catalog.domain.exception.ProductNotFoundException;
import br.com.miriageekstore.catalog.domain.model.CategoryId;
import br.com.miriageekstore.catalog.domain.model.Product;
import br.com.miriageekstore.catalog.domain.model.ProductVariant;
import br.com.miriageekstore.catalog.domain.model.Sku;
import br.com.miriageekstore.catalog.domain.port.in.UpdateVariantStatusCommand;
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
class UpdateVariantStatusUseCaseTest {

    @Mock ProductRepository productRepository;
    @InjectMocks UpdateVariantStatusUseCaseImpl useCase;

    @Test
    void shouldDeactivateVariantWhenProductHasMultipleActive() {
        var v1 = ProductVariant.create("Cor", "Preto", BigDecimal.valueOf(99), 5, new Sku("SKU-001"));
        var v2 = ProductVariant.create("Cor", "Branco", BigDecimal.valueOf(99), 5, new Sku("SKU-002"));
        var product = Product.create("Funko Batman", "Desc", CategoryId.of(UUID.randomUUID()), false, List.of(v1, v2));
        var variantId = v1.getId().value();

        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(new UpdateVariantStatusCommand(product.getId().value(), variantId, false));

        assertThat(result.active()).isFalse();
        verify(productRepository).save(any());
    }

    @Test
    void shouldActivateVariant() {
        var v1 = ProductVariant.create("Cor", "Preto", BigDecimal.valueOf(99), 5, new Sku("SKU-001"));
        var v2 = ProductVariant.create("Cor", "Branco", BigDecimal.valueOf(99), 5, new Sku("SKU-002"));
        var product = Product.create("Funko Batman", "Desc", CategoryId.of(UUID.randomUUID()), false, List.of(v1, v2));
        product.changeVariantStatus(v1.getId(), false);
        var variantId = v1.getId().value();

        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(new UpdateVariantStatusCommand(product.getId().value(), variantId, true));

        assertThat(result.active()).isTrue();
    }

    @Test
    void shouldThrowWhenTryingToDeactivateLastActiveVariant() {
        var v = ProductVariant.create("Cor", "Preto", BigDecimal.valueOf(99), 5, new Sku("SKU-001"));
        var product = Product.create("Funko Batman", "Desc", CategoryId.of(UUID.randomUUID()), false, List.of(v));
        var variantId = v.getId().value();

        when(productRepository.findById(any())).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> useCase.execute(
                new UpdateVariantStatusCommand(product.getId().value(), variantId, false)))
                .isInstanceOf(LastActiveVariantException.class);

        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenProductNotFound() {
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(
                new UpdateVariantStatusCommand(UUID.randomUUID(), UUID.randomUUID(), false)))
                .isInstanceOf(ProductNotFoundException.class);
    }
}
