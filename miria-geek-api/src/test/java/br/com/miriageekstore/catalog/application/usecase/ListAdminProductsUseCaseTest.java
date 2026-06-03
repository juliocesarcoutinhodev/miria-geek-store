package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.port.in.AdminProductQuery;
import br.com.miriageekstore.catalog.domain.port.in.ListAdminProductsResult;
import br.com.miriageekstore.catalog.domain.port.out.ProductCatalogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListAdminProductsUseCaseTest {

    @Mock ProductCatalogRepository repository;
    @InjectMocks ListAdminProductsUseCaseImpl useCase;

    @Test
    void shouldDelegateToRepository() {
        var query    = new AdminProductQuery(null, null, null, null, 0, 20, "mais_recente");
        var expected = new ListAdminProductsResult(List.of(), 0, 20, 0, 0);
        when(repository.searchForAdmin(query)).thenReturn(expected);

        var result = useCase.execute(query);

        assertThat(result).isEqualTo(expected);
        verify(repository).searchForAdmin(query);
    }

    @Test
    void shouldReturnAllProductsRegardlessOfStatus() {
        var categoryId = UUID.randomUUID();
        var query = new AdminProductQuery(null, null, null, null, 0, 20, "mais_recente");

        var active = item(categoryId, "ACTIVE");
        var inactive = item(categoryId, "INACTIVE");
        var expected = new ListAdminProductsResult(List.of(active, inactive), 0, 20, 2, 1);
        when(repository.searchForAdmin(query)).thenReturn(expected);

        var result = useCase.execute(query);

        assertThat(result.content()).hasSize(2);
        assertThat(result.content()).extracting(ListAdminProductsResult.AdminProductItem::status)
                .containsExactlyInAnyOrder("ACTIVE", "INACTIVE");
    }

    @Test
    void shouldFilterByStatus() {
        var query = new AdminProductQuery(null, null, "INACTIVE", null, 0, 20, "mais_recente");
        var expected = new ListAdminProductsResult(
                List.of(item(UUID.randomUUID(), "INACTIVE")), 0, 20, 1, 1);
        when(repository.searchForAdmin(query)).thenReturn(expected);

        var result = useCase.execute(query);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).status()).isEqualTo("INACTIVE");
    }

    @Test
    void shouldIncludeAggregatesInResult() {
        var query = new AdminProductQuery(null, null, null, null, 0, 20, "mais_recente");
        var categoryId = UUID.randomUUID();
        var item = new ListAdminProductsResult.AdminProductItem(
                UUID.randomUUID(), "Funko Pop Batman", "funko-pop-batman",
                categoryId, "Action Figures", "ACTIVE", true,
                3, 2, 150, Instant.now());
        var expected = new ListAdminProductsResult(List.of(item), 0, 20, 1, 1);
        when(repository.searchForAdmin(query)).thenReturn(expected);

        var result = useCase.execute(query);

        var product = result.content().get(0);
        assertThat(product.totalVariants()).isEqualTo(3);
        assertThat(product.totalImages()).isEqualTo(2);
        assertThat(product.totalStock()).isEqualTo(150);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private ListAdminProductsResult.AdminProductItem item(UUID categoryId, String status) {
        return new ListAdminProductsResult.AdminProductItem(
                UUID.randomUUID(), "Produto Teste", "produto-teste",
                categoryId, "Categoria", status, false,
                1, 0, 10, Instant.now());
    }
}
