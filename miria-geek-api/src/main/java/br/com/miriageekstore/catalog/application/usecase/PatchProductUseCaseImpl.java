package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.event.ProductUpdated;
import br.com.miriageekstore.catalog.domain.exception.CategoryNotFoundException;
import br.com.miriageekstore.catalog.domain.exception.ProductNotFoundException;
import br.com.miriageekstore.catalog.domain.model.CategoryId;
import br.com.miriageekstore.catalog.domain.port.in.PatchProductCommand;
import br.com.miriageekstore.catalog.domain.port.in.PatchProductUseCase;
import br.com.miriageekstore.catalog.domain.port.in.UpdateProductResult;
import br.com.miriageekstore.catalog.domain.port.out.CatalogEventPublisher;
import br.com.miriageekstore.catalog.domain.port.out.CategoryRepository;
import br.com.miriageekstore.catalog.domain.port.out.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PatchProductUseCaseImpl implements PatchProductUseCase {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CatalogEventPublisher eventPublisher;

    @Override
    @Transactional
    public UpdateProductResult execute(PatchProductCommand command) {
        var product = productRepository.findById(command.id())
                .orElseThrow(ProductNotFoundException::new);

        if (command.categoryId() != null) {
            var newCategoryId = CategoryId.of(command.categoryId());
            if (categoryRepository.findById(newCategoryId).isEmpty()) {
                throw new CategoryNotFoundException();
            }
        }

        var newCategoryId = command.categoryId() != null
                ? CategoryId.of(command.categoryId()) : null;

        product.partialUpdate(command.name(), command.description(), newCategoryId, command.featured());
        var saved = productRepository.save(product);

        eventPublisher.publish(new ProductUpdated(
                saved.getId().value(), saved.getName(), saved.getSlug().value(),
                saved.getCategoryId().value(), saved.isFeatured(), Instant.now()));

        return UpdateProductUseCaseImpl.toResult(saved);
    }
}
