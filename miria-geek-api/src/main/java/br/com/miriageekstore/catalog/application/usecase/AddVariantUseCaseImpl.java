package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.ProductNotFoundException;
import br.com.miriageekstore.catalog.domain.exception.ProductSkuAlreadyExistsException;
import br.com.miriageekstore.catalog.domain.model.ProductId;
import br.com.miriageekstore.catalog.domain.model.ProductVariant;
import br.com.miriageekstore.catalog.domain.model.Sku;
import br.com.miriageekstore.catalog.domain.port.in.AddVariantCommand;
import br.com.miriageekstore.catalog.domain.port.in.AddVariantResult;
import br.com.miriageekstore.catalog.domain.port.in.AddVariantUseCase;
import br.com.miriageekstore.catalog.domain.port.out.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddVariantUseCaseImpl implements AddVariantUseCase {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public AddVariantResult execute(AddVariantCommand command) {
        var product = productRepository.findById(ProductId.of(command.productId()))
                .orElseThrow(ProductNotFoundException::new);

        var sku = (command.sku() != null && !command.sku().isBlank())
                ? new Sku(command.sku())
                : Sku.generate(product.getSlug(), command.attributeName(), command.attributeValue());

        if (productRepository.existsBySku(sku.value())) {
            throw new ProductSkuAlreadyExistsException(sku.value());
        }

        var variant = ProductVariant.create(
                command.attributeName(), command.attributeValue(),
                command.price(), command.stock(), sku);

        product.addVariant(variant);
        productRepository.save(product);

        return new AddVariantResult(
                variant.getId().value(), variant.getAttributeName(), variant.getAttributeValue(),
                variant.getPrice(), variant.getStock(), variant.getSku().value(),
                variant.isActive(), variant.getCreatedAt());
    }
}
