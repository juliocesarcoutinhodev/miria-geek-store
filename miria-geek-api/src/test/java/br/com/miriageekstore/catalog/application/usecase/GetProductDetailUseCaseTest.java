package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.ProductNotFoundException;
import br.com.miriageekstore.catalog.domain.port.in.GetProductDetailResult;
import br.com.miriageekstore.catalog.domain.port.out.ProductCatalogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProductDetailUseCaseTest {

    @Mock ProductCatalogRepository repository;
    @InjectMocks GetProductDetailUseCaseImpl useCase;

    @Test
    void shouldReturnProductDetailWhenFound() {
        var detail = detail("funko-pop-batman");
        when(repository.findBySlug("funko-pop-batman")).thenReturn(Optional.of(detail));

        var result = useCase.execute("funko-pop-batman");

        assertThat(result.slug()).isEqualTo("funko-pop-batman");
        assertThat(result.name()).isEqualTo("Funko Pop Batman");
        assertThat(result.status()).isEqualTo("ACTIVE");
        assertThat(result.images()).hasSize(1);
        assertThat(result.variants()).hasSize(2);
        verify(repository).findBySlug("funko-pop-batman");
    }

    @Test
    void shouldThrowWhenSlugNotFound() {
        when(repository.findBySlug("slug-inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("slug-inexistente"))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void shouldThrowForInactiveProduct() {
        // adapter returns empty for INACTIVE products — use case treats both cases as 404
        when(repository.findBySlug("produto-inativo")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("produto-inativo"))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void shouldReturnOnlyActiveVariants() {
        var detail = detail("funko-pop-batman");
        when(repository.findBySlug("funko-pop-batman")).thenReturn(Optional.of(detail));

        var result = useCase.execute("funko-pop-batman");

        assertThat(result.variants()).allMatch(GetProductDetailResult.VariantItem::active);
    }

    @Test
    void shouldMarkVariantUnavailableWhenStockIsZero() {
        var detail = detail("funko-pop-batman");
        when(repository.findBySlug("funko-pop-batman")).thenReturn(Optional.of(detail));

        var result = useCase.execute("funko-pop-batman");

        var outOfStock = result.variants().stream()
                .filter(v -> v.stock() == 0)
                .toList();
        assertThat(outOfStock).allMatch(v -> !v.available());

        var inStock = result.variants().stream()
                .filter(v -> v.stock() > 0)
                .toList();
        assertThat(inStock).allMatch(GetProductDetailResult.VariantItem::available);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private GetProductDetailResult detail(String slug) {
        var categoryId = UUID.randomUUID();
        var image = new GetProductDetailResult.ImageItem(UUID.randomUUID(), "http://img.jpg", true, 1);

        var variantInStock    = new GetProductDetailResult.VariantItem(
                UUID.randomUUID(), "Cor", "Preto", BigDecimal.valueOf(89.90), 10, "SKU-001", true, true);
        var variantOutOfStock = new GetProductDetailResult.VariantItem(
                UUID.randomUUID(), "Cor", "Branco", BigDecimal.valueOf(89.90), 0, "SKU-002", true, false);

        return new GetProductDetailResult(
                UUID.randomUUID(), "Funko Pop Batman", slug, "Boneco colecionável",
                categoryId, "Action Figures", "ACTIVE", false, Instant.now(),
                List.of(image), List.of(variantInStock, variantOutOfStock));
    }
}
