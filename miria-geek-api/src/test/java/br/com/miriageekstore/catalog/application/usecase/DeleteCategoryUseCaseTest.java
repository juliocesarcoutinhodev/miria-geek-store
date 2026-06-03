package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.CategoryHasProductsException;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteCategoryUseCaseTest {

    @Mock CategoryRepository categoryRepository;
    @InjectMocks DeleteCategoryUseCaseImpl useCase;

    @Test
    void shouldDeleteCategorySuccessfully() {
        var category = Category.create("Anime", "Desc");
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryRepository.hasProducts(category.getId())).thenReturn(false);

        assertThatCode(() -> useCase.execute(category.getId())).doesNotThrowAnyException();

        verify(categoryRepository).deleteById(category.getId());
    }

    @Test
    void shouldThrowWhenCategoryNotFound() {
        var id = CategoryId.generate();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id))
                .isInstanceOf(CategoryNotFoundException.class);

        verify(categoryRepository, never()).deleteById(id);
    }

    @Test
    void shouldThrowWhenCategoryHasProducts() {
        var category = Category.create("Anime", "Desc");
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryRepository.hasProducts(category.getId())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(category.getId()))
                .isInstanceOf(CategoryHasProductsException.class);

        verify(categoryRepository, never()).deleteById(category.getId());
    }
}
