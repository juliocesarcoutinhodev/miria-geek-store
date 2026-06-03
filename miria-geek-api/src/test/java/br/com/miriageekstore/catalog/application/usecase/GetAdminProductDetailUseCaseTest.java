package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.ProductNotFoundException;
import br.com.miriageekstore.catalog.domain.port.in.GetAdminProductDetailResult;
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
class GetAdminProductDetailUseCaseTest {

    @Mock ProductCatalogRepository repository;
    @InjectMocks GetAdminProductDetailUseCaseImpl useCase;

    @Test
    void shouldReturnDetailWithAllVariants() {
        var id     = UUID.randomUUID();
        var detail = detail(id, "ACTIVE");
        when(repository.findByIdForAdmin(id)).thenReturn(Optional.of(detail));

        var result = useCase.execute(id);

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.status()).isEqualTo("ACTIVE");
        assertThat(result.variants()).hasSize(2);
        verify(repository).findByIdForAdmin(id);
    }

    @Test
    void shouldReturnInactiveProduct() {
        var id     = UUID.randomUUID();
        var detail = detail(id, "INACTIVE");
        when(repository.findByIdForAdmin(id)).thenReturn(Optional.of(detail));

        var result = useCase.execute(id);

        assertThat(result.status()).isEqualTo("INACTIVE");
    }

    @Test
    void shouldIncludeActiveAndInactiveVariants() {
        var id     = UUID.randomUUID();
        var detail = detail(id, "ACTIVE");
        when(repository.findByIdForAdmin(id)).thenReturn(Optional.of(detail));

        var result = useCase.execute(id);

        assertThat(result.variants()).anyMatch(v -> v.active());
        assertThat(result.variants()).anyMatch(v -> !v.active());
    }

    @Test
    void shouldThrowWhenProductNotFound() {
        var id = UUID.randomUUID();
        when(repository.findByIdForAdmin(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id))
                .isInstanceOf(ProductNotFoundException.class);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private GetAdminProductDetailResult detail(UUID id, String status) {
        var categoryId = UUID.randomUUID();
        var image = new GetAdminProductDetailResult.ImageItem(
                UUID.randomUUID(), "http://img.jpg", true, 1);

        var activeVariant = new GetAdminProductDetailResult.VariantItem(
                UUID.randomUUID(), "Cor", "Preto", BigDecimal.valueOf(89.90), 10,
                "SKU-001", true, Instant.now());
        var inactiveVariant = new GetAdminProductDetailResult.VariantItem(
                UUID.randomUUID(), "Cor", "Branco", BigDecimal.valueOf(89.90), 5,
                "SKU-002", false, Instant.now());

        return new GetAdminProductDetailResult(
                id, "Funko Pop Batman", "funko-pop-batman", "Descrição",
                categoryId, "Action Figures", status, false, Instant.now(),
                List.of(image), List.of(activeVariant, inactiveVariant));
    }
}
