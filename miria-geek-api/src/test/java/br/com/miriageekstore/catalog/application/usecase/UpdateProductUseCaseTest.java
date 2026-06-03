package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.CategoryNotFoundException;
import br.com.miriageekstore.catalog.domain.exception.ProductNotFoundException;
import br.com.miriageekstore.catalog.domain.model.Category;
import br.com.miriageekstore.catalog.domain.model.CategoryId;
import br.com.miriageekstore.catalog.domain.model.Product;
import br.com.miriageekstore.catalog.domain.model.ProductId;
import br.com.miriageekstore.catalog.domain.model.Sku;
import br.com.miriageekstore.catalog.domain.model.Slug;
import br.com.miriageekstore.catalog.domain.model.ProductVariant;
import br.com.miriageekstore.catalog.domain.port.in.UpdateProductCommand;
import br.com.miriageekstore.catalog.domain.port.out.CatalogEventPublisher;
import br.com.miriageekstore.catalog.domain.port.out.CategoryRepository;
import br.com.miriageekstore.catalog.domain.port.out.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateProductUseCaseTest {

    @Mock ProductRepository productRepository;
    @Mock CategoryRepository categoryRepository;
    @Mock CatalogEventPublisher eventPublisher;
    @InjectMocks UpdateProductUseCaseImpl useCase;

    @Test
    void shouldUpdateProductSuccessfully() {
        var product = product();
        var newCategory = Category.create("Nova Categoria", "Desc");
        var cmd = new UpdateProductCommand(product.getId(), "Funko Pop Coringa",
                "Novo boneco", newCategory.getId().value(), true);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(categoryRepository.findById(newCategory.getId())).thenReturn(Optional.of(newCategory));
        when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(cmd);

        assertThat(result.name()).isEqualTo("Funko Pop Coringa");
        assertThat(result.slug()).isEqualTo("funko-pop-coringa");
        assertThat(result.featured()).isTrue();
        assertThat(result.categoryId()).isEqualTo(newCategory.getId().value());
        verify(eventPublisher).publish(any());
    }

    @Test
    void shouldRegenerateSlugWhenNameChanges() {
        var product = product();
        var category = Category.create("Cat", "Desc");
        var cmd = new UpdateProductCommand(product.getId(), "Novo Nome do Produto",
                "Desc", category.getId().value(), false);

        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(categoryRepository.findById(any())).thenReturn(Optional.of(category));
        when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(cmd);

        assertThat(result.slug()).isEqualTo("novo-nome-do-produto");
    }

    @Test
    void shouldThrowWhenProductNotFound() {
        when(productRepository.findById(any())).thenReturn(Optional.empty());
        var cmd = new UpdateProductCommand(ProductId.generate(), "Nome", "Desc",
                CategoryId.generate().value(), false);

        assertThatThrownBy(() -> useCase.execute(cmd))
                .isInstanceOf(ProductNotFoundException.class);

        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCategoryNotFound() {
        var product = product();
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(categoryRepository.findById(any())).thenReturn(Optional.empty());
        var cmd = new UpdateProductCommand(product.getId(), "Nome", "Desc",
                CategoryId.generate().value(), false);

        assertThatThrownBy(() -> useCase.execute(cmd))
                .isInstanceOf(CategoryNotFoundException.class);

        verify(productRepository, never()).save(any());
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Product product() {
        var slug = Slug.from("Funko Pop Batman");
        var sku = Sku.generate(slug, "Cor", "Preto");
        return Product.create("Funko Pop Batman", "Desc",
                CategoryId.generate(), false,
                List.of(ProductVariant.create("Cor", "Preto", BigDecimal.TEN, 5, sku)));
    }
}
