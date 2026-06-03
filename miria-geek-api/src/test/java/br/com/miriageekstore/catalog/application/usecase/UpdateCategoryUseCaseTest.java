package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.CategoryNameAlreadyExistsException;
import br.com.miriageekstore.catalog.domain.exception.CategoryNotFoundException;
import br.com.miriageekstore.catalog.domain.model.Category;
import br.com.miriageekstore.catalog.domain.model.CategoryId;
import br.com.miriageekstore.catalog.domain.port.in.UpdateCategoryCommand;
import br.com.miriageekstore.catalog.domain.port.out.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateCategoryUseCaseTest {

    @Mock CategoryRepository categoryRepository;
    @InjectMocks UpdateCategoryUseCaseImpl useCase;

    @Test
    void shouldUpdateCategorySuccessfully() {
        var category = Category.create("Action Figures", "Descrição antiga");
        var cmd = new UpdateCategoryCommand(category.getId(), "Mangá e Anime", "Nova descrição", false);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameExcludingId(cmd.name(), cmd.id())).thenReturn(false);
        when(categoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(cmd);

        assertThat(result.name()).isEqualTo("Mangá e Anime");
        assertThat(result.slug()).isEqualTo("manga-e-anime");
        assertThat(result.active()).isFalse();
        verify(categoryRepository).save(category);
    }

    @Test
    void shouldThrowWhenCategoryNotFound() {
        var id = CategoryId.generate();
        var cmd = new UpdateCategoryCommand(id, "Anime", "Desc", true);
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(cmd))
                .isInstanceOf(CategoryNotFoundException.class);

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenNameConflictsWithAnotherCategory() {
        var category = Category.create("Action Figures", "Desc");
        var cmd = new UpdateCategoryCommand(category.getId(), "Anime", "Desc", true);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameExcludingId(cmd.name(), cmd.id())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(cmd))
                .isInstanceOf(CategoryNameAlreadyExistsException.class)
                .hasMessageContaining("Anime");

        verify(categoryRepository, never()).save(any());
    }
}
