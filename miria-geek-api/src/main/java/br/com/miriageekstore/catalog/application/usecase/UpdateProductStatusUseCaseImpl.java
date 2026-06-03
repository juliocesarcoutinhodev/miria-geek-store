package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.event.ProductStatusChanged;
import br.com.miriageekstore.catalog.domain.exception.ProductCannotBeActivatedException;
import br.com.miriageekstore.catalog.domain.exception.ProductNotFoundException;
import br.com.miriageekstore.catalog.domain.model.ProductStatus;
import br.com.miriageekstore.catalog.domain.port.in.UpdateProductStatusCommand;
import br.com.miriageekstore.catalog.domain.port.in.UpdateProductStatusResult;
import br.com.miriageekstore.catalog.domain.port.in.UpdateProductStatusUseCase;
import br.com.miriageekstore.catalog.domain.port.out.CatalogEventPublisher;
import br.com.miriageekstore.catalog.domain.port.out.CategoryRepository;
import br.com.miriageekstore.catalog.domain.port.out.ProductImageRepository;
import br.com.miriageekstore.catalog.domain.port.out.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UpdateProductStatusUseCaseImpl implements UpdateProductStatusUseCase {

    private final ProductRepository productRepository;
    private final ProductImageRepository imageRepository;
    private final CategoryRepository categoryRepository;
    private final CatalogEventPublisher eventPublisher;

    @Override
    @Transactional
    public UpdateProductStatusResult execute(UpdateProductStatusCommand command) {
        var product = productRepository.findById(command.id())
                .orElseThrow(ProductNotFoundException::new);

        if (command.status() == ProductStatus.ACTIVE) {
            var reasons = new ArrayList<String>();

            if (imageRepository.countByProductId(command.id()) == 0) {
                reasons.add("o produto não possui imagens cadastradas");
            }

            categoryRepository.findById(product.getCategoryId()).ifPresentOrElse(
                    category -> {
                        if (!category.isActive()) reasons.add("a categoria do produto está inativa");
                    },
                    () -> reasons.add("a categoria do produto não foi encontrada")
            );

            if (!reasons.isEmpty()) {
                throw new ProductCannotBeActivatedException(reasons);
            }
        }

        product.changeStatus(command.status());
        var saved = productRepository.save(product);

        eventPublisher.publish(new ProductStatusChanged(
                saved.getId().value(), saved.getName(), saved.getStatus().name(), Instant.now()));

        return new UpdateProductStatusResult(
                saved.getId().value(), saved.getName(), saved.getStatus().name());
    }
}
