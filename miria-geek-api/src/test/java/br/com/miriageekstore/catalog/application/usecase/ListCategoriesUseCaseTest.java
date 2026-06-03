package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.model.Category;
import br.com.miriageekstore.catalog.domain.port.in.ListCategoriesQuery;
import br.com.miriageekstore.catalog.domain.port.out.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListCategoriesUseCaseTest {

    @Mock CategoryRepository categoryRepository;
    @InjectMocks ListCategoriesUseCaseImpl useCase;

    @Test
    void shouldReturnPagedCategories() {
        var anime = Category.create("Anime", "Animes importados");
        var figures = Category.create("Action Figures", "Bonecos colecionáveis");
        var page = new CategoryRepository.CategoryPage(List.of(anime, figures), 0, 20, 2L, 1);

        when(categoryRepository.findAll(isNull(), isNull(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(page);

        var query = new ListCategoriesQuery(null, null, 0, 20, "name", "asc");
        var result = useCase.execute(query);

        assertThat(result.content()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(2L);
        assertThat(result.page()).isZero();
        assertThat(result.content()).extracting("name")
                .containsExactlyInAnyOrder("Anime", "Action Figures");
    }

    @Test
    void shouldReturnEmptyPageWhenNoCategoriesMatch() {
        var emptyPage = new CategoryRepository.CategoryPage(List.of(), 0, 20, 0L, 0);

        when(categoryRepository.findAll(anyString(), any(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(emptyPage);

        var query = new ListCategoriesQuery("inexistente", null, 0, 20, "name", "asc");
        var result = useCase.execute(query);

        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isZero();
    }
}
