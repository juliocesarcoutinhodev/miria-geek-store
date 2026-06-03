package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.event.ProductUpdated;
import br.com.miriageekstore.catalog.domain.exception.CategoryNotFoundException;
import br.com.miriageekstore.catalog.domain.exception.ProductNotFoundException;
import br.com.miriageekstore.catalog.domain.model.CategoryId;
import br.com.miriageekstore.catalog.domain.model.Product;
import br.com.miriageekstore.catalog.domain.port.in.UpdateProductCommand;
import br.com.miriageekstore.catalog.domain.port.in.UpdateProductResult;
import br.com.miriageekstore.catalog.domain.port.in.UpdateProductUseCase;
import br.com.miriageekstore.catalog.domain.port.out.CatalogEventPublisher;
import br.com.miriageekstore.catalog.domain.port.out.CategoryRepository;
import br.com.miriageekstore.catalog.domain.port.out.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UpdateProductUseCaseImpl implements UpdateProductUseCase {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CatalogEventPublisher eventPublisher;

    @Override
    @Transactional
    public UpdateProductResult execute(UpdateProductCommand command) {
        var product = productRepository.findById(command.id())
                .orElseThrow(ProductNotFoundException::new);

        var newCategoryId = CategoryId.of(command.categoryId());
        if (categoryRepository.findById(newCategoryId).isEmpty()) {
            throw new CategoryNotFoundException();
        }

        product.update(command.name(), command.description(), newCategoryId, command.featured());
        var saved = productRepository.save(product);

        eventPublisher.publish(new ProductUpdated(
                saved.getId().value(), saved.getName(), saved.getSlug().value(),
                saved.getCategoryId().value(), saved.isFeatured(), Instant.now()));

        return toResult(saved);
    }

    static UpdateProductResult toResult(Product product) {
        var variants = product.getVariants().stream()
                .map(v -> new UpdateProductResult.VariantSummary(
                        v.getId().value(), v.getAttributeName(), v.getAttributeValue(),
                        v.getPrice(), v.getStock(), v.getSku().value(), v.getCreatedAt()))
                .toList();

        return new UpdateProductResult(
                product.getId().value(), product.getName(), product.getSlug().value(),
                product.getDescription(), product.getCategoryId().value(),
                product.getStatus().name(), product.isFeatured(),
                variants, product.getCreatedAt()
        );
    }
}
