package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.ProductNotFoundException;
import br.com.miriageekstore.catalog.domain.model.ProductId;
import br.com.miriageekstore.catalog.domain.model.VariantId;
import br.com.miriageekstore.catalog.domain.port.in.UpdateVariantStatusCommand;
import br.com.miriageekstore.catalog.domain.port.in.UpdateVariantStatusResult;
import br.com.miriageekstore.catalog.domain.port.in.UpdateVariantStatusUseCase;
import br.com.miriageekstore.catalog.domain.port.out.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateVariantStatusUseCaseImpl implements UpdateVariantStatusUseCase {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public UpdateVariantStatusResult execute(UpdateVariantStatusCommand command) {
        var product = productRepository.findById(ProductId.of(command.productId()))
                .orElseThrow(ProductNotFoundException::new);

        var variantId = VariantId.of(command.variantId());
        product.changeVariantStatus(variantId, command.active());
        productRepository.save(product);

        var variant = product.findVariantOrThrow(variantId);
        return new UpdateVariantStatusResult(
                variant.getId().value(), variant.getAttributeName(), variant.getAttributeValue(),
                variant.getPrice(), variant.getStock(), variant.getSku().value(),
                variant.isActive(), variant.getCreatedAt(),
                variant.getWeight(), variant.getWidth(), variant.getHeight(), variant.getDepth());
    }
}
