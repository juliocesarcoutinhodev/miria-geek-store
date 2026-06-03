package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.CategoryNotFoundException;
import br.com.miriageekstore.catalog.domain.model.Category;
import br.com.miriageekstore.catalog.domain.model.CategoryId;
import br.com.miriageekstore.catalog.domain.port.out.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCategoryByIdUseCaseTest {

    @Mock CategoryRepository categoryRepository;
    @InjectMocks GetCategoryByIdUseCaseImpl useCase;

    @Test
    void shouldReturnCategoryById() {
        var category = Category.create("Action Figures", "Bonecos colecionáveis");
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        var result = useCase.execute(category.getId());

        assertThat(result.id()).isEqualTo(category.getId().value());
        assertThat(result.name()).isEqualTo("Action Figures");
        assertThat(result.slug()).isEqualTo("action-figures");
        assertThat(result.active()).isTrue();
        assertThat(result.totalProducts()).isZero();
    }

    @Test
    void shouldThrowWhenCategoryNotFound() {
        var id = CategoryId.generate();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id))
                .isInstanceOf(CategoryNotFoundException.class);
    }
}
