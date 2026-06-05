package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.ProductNotFoundException;
import br.com.miriageekstore.catalog.domain.exception.ProductSkuAlreadyExistsException;
import br.com.miriageekstore.catalog.domain.model.ProductId;
import br.com.miriageekstore.catalog.domain.model.Sku;
import br.com.miriageekstore.catalog.domain.model.VariantId;
import br.com.miriageekstore.catalog.domain.port.in.UpdateVariantCommand;
import br.com.miriageekstore.catalog.domain.port.in.UpdateVariantResult;
import br.com.miriageekstore.catalog.domain.port.in.UpdateVariantUseCase;
import br.com.miriageekstore.catalog.domain.port.out.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateVariantUseCaseImpl implements UpdateVariantUseCase {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public UpdateVariantResult execute(UpdateVariantCommand command) {
        var product = productRepository.findById(ProductId.of(command.productId()))
                .orElseThrow(ProductNotFoundException::new);

        var variantId = VariantId.of(command.variantId());
        var variant = product.findVariantOrThrow(variantId);

        var newSku = (command.sku() != null && !command.sku().isBlank())
                ? new Sku(command.sku())
                : variant.getSku();

        if (!newSku.value().equals(variant.getSku().value()) &&
                productRepository.existsBySkuExcluding(newSku.value(), command.variantId())) {
            throw new ProductSkuAlreadyExistsException(newSku.value());
        }

        product.updateVariant(variantId, command.attributeName(), command.attributeValue(),
                command.price(), command.stock(), newSku,
                command.weight(), command.width(), command.height(), command.depth());
        productRepository.save(product);

        return new UpdateVariantResult(
                variant.getId().value(), variant.getAttributeName(), variant.getAttributeValue(),
                variant.getPrice(), variant.getStock(), variant.getSku().value(),
                variant.isActive(), variant.getCreatedAt(),
                variant.getWeight(), variant.getWidth(), variant.getHeight(), variant.getDepth());
    }
}
