package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.CategoryNameAlreadyExistsException;
import br.com.miriageekstore.catalog.domain.model.Category;
import br.com.miriageekstore.catalog.domain.port.in.CreateCategoryCommand;
import br.com.miriageekstore.catalog.domain.port.out.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCategoryUseCaseTest {

    @Mock CategoryRepository categoryRepository;
    @InjectMocks CreateCategoryUseCaseImpl useCase;

    private static final CreateCategoryCommand CMD =
            new CreateCategoryCommand("Action Figures", "Bonecos e miniaturas colecionáveis");

    @Test
    void shouldCreateCategorySuccessfully() {
        when(categoryRepository.existsByName(CMD.name())).thenReturn(false);
        when(categoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(CMD);

        assertThat(result.name()).isEqualTo("Action Figures");
        assertThat(result.slug()).isEqualTo("action-figures");
        assertThat(result.description()).isEqualTo("Bonecos e miniaturas colecionáveis");
        assertThat(result.active()).isTrue();
        assertThat(result.id()).isNotNull();
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void shouldThrowWhenNameAlreadyExists() {
        when(categoryRepository.existsByName(CMD.name())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(CMD))
                .isInstanceOf(CategoryNameAlreadyExistsException.class)
                .hasMessageContaining("Action Figures");

        verify(categoryRepository, never()).save(any());
    }
}
