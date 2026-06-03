package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import br.com.miriageekstore.catalog.domain.port.in.CreateProductCommand;
import br.com.miriageekstore.catalog.domain.port.in.CreateProductResult;
import org.springframework.stereotype.Component;

@Component
class ProductWebMapper {

    CreateProductCommand toCommand(CreateProductRequest request) {
        var variants = request.variants().stream()
                .map(v -> new CreateProductCommand.VariantInput(
                        v.attributeName(), v.attributeValue(),
                        v.price(), v.stock(), v.sku()))
                .toList();

        return new CreateProductCommand(
                request.name(),
                request.description(),
                request.categoryId(),
                Boolean.TRUE.equals(request.featured()),
                variants
        );
    }

    ProductResponse toResponse(CreateProductResult result) {
        var variants = result.variants().stream()
                .map(v -> new ProductResponse.VariantResponse(
                        v.id(), v.attributeName(), v.attributeValue(),
                        v.price(), v.stock(), v.sku(), v.createdAt()))
                .toList();

        return new ProductResponse(
                result.id(), result.name(), result.slug(), result.description(),
                result.categoryId(), result.status(), result.featured(),
                variants, result.createdAt()
        );
    }
}
