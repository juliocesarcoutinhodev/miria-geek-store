package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.port.in.ListProductsResult;
import br.com.miriageekstore.catalog.domain.port.in.ProductSearchQuery;
import br.com.miriageekstore.catalog.domain.port.out.ProductCatalogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListProductsUseCaseTest {

    @Mock ProductCatalogRepository repository;
    @InjectMocks ListProductsUseCaseImpl useCase;

    @Test
    void shouldDelegateToRepository() {
        var query = new ProductSearchQuery(null, null, null, null, null, 0, 20, "mais_recente");
        var expected = new ListProductsResult(List.of(), 0, 20, 0, 0);
        when(repository.search(query)).thenReturn(expected);

        var result = useCase.execute(query);

        assertThat(result).isEqualTo(expected);
        verify(repository).search(query);
    }

    @Test
    void shouldPassAllFiltersToRepository() {
        var categoryId = UUID.randomUUID();
        var query = new ProductSearchQuery("batman", categoryId,
                BigDecimal.valueOf(50), BigDecimal.valueOf(200), true, 1, 10, "preco_asc");

        var item = new ListProductsResult.ProductItem(
                UUID.randomUUID(), "Funko Pop Batman", "funko-pop-batman",
                categoryId, "Action Figures", null,
                BigDecimal.valueOf(89.90), BigDecimal.valueOf(149.90), 2, true);
        var expected = new ListProductsResult(List.of(item), 1, 10, 1, 1);
        when(repository.search(query)).thenReturn(expected);

        var result = useCase.execute(query);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).name()).isEqualTo("Funko Pop Batman");
        assertThat(result.totalElements()).isEqualTo(1);
        verify(repository).search(query);
    }

    @Test
    void shouldReturnEmptyPageWhenNoProductsMatch() {
        var query = new ProductSearchQuery("produto-inexistente", null, null, null, null, 0, 20, "mais_recente");
        var expected = new ListProductsResult(List.of(), 0, 20, 0, 0);
        when(repository.search(query)).thenReturn(expected);

        var result = useCase.execute(query);

        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isZero();
        assertThat(result.totalPages()).isZero();
    }
}
