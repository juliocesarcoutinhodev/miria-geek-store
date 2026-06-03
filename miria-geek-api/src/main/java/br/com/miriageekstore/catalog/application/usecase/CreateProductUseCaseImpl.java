package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.CategoryNotFoundException;
import br.com.miriageekstore.catalog.domain.exception.ProductSkuAlreadyExistsException;
import br.com.miriageekstore.catalog.domain.model.CategoryId;
import br.com.miriageekstore.catalog.domain.model.Product;
import br.com.miriageekstore.catalog.domain.model.ProductVariant;
import br.com.miriageekstore.catalog.domain.model.Sku;
import br.com.miriageekstore.catalog.domain.model.Slug;
import br.com.miriageekstore.catalog.domain.port.in.CreateProductCommand;
import br.com.miriageekstore.catalog.domain.port.in.CreateProductResult;
import br.com.miriageekstore.catalog.domain.port.in.CreateProductUseCase;
import br.com.miriageekstore.catalog.domain.port.out.CategoryRepository;
import br.com.miriageekstore.catalog.domain.port.out.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateProductUseCaseImpl implements CreateProductUseCase {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CreateProductResult execute(CreateProductCommand command) {
        var categoryId = CategoryId.of(command.categoryId());
        if (categoryRepository.findById(categoryId).isEmpty()) {
            throw new CategoryNotFoundException();
        }

        var productSlug = Slug.from(command.name());
        var variants = buildVariants(command, productSlug);
        var product = Product.create(command.name(), command.description(), categoryId,
                command.featured(), variants);
        var saved = productRepository.save(product);

        return toResult(saved);
    }

    private List<ProductVariant> buildVariants(CreateProductCommand command, Slug productSlug) {
        return command.variants().stream().map(input -> {
            var sku = (input.sku() != null && !input.sku().isBlank())
                    ? new Sku(input.sku())
                    : Sku.generate(productSlug, input.attributeName(), input.attributeValue());

            if (productRepository.existsBySku(sku.value())) {
                throw new ProductSkuAlreadyExistsException(sku.value());
            }

            return ProductVariant.create(input.attributeName(), input.attributeValue(),
                    input.price(), input.stock(), sku);
        }).toList();
    }

    private CreateProductResult toResult(Product product) {
        var variantSummaries = product.getVariants().stream()
                .map(v -> new CreateProductResult.VariantSummary(
                        v.getId().value(), v.getAttributeName(), v.getAttributeValue(),
                        v.getPrice(), v.getStock(), v.getSku().value(), v.isActive(), v.getCreatedAt()))
                .toList();

        return new CreateProductResult(
                product.getId().value(), product.getName(), product.getSlug().value(),
                product.getDescription(), product.getCategoryId().value(),
                product.getStatus().name(), product.isFeatured(),
                variantSummaries, product.getCreatedAt()
        );
    }
}
