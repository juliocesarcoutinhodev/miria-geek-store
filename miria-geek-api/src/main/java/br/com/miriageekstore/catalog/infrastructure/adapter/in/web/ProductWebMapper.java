package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import br.com.miriageekstore.catalog.domain.model.ProductId;
import br.com.miriageekstore.catalog.domain.port.in.CreateProductCommand;
import br.com.miriageekstore.catalog.domain.port.in.CreateProductResult;
import br.com.miriageekstore.catalog.domain.port.in.PatchProductCommand;
import br.com.miriageekstore.catalog.domain.port.in.ProductImageResult;
import br.com.miriageekstore.catalog.domain.port.in.ReorderProductImagesCommand;
import br.com.miriageekstore.catalog.domain.port.in.UpdateProductCommand;
import br.com.miriageekstore.catalog.domain.port.in.UpdateProductResult;
import br.com.miriageekstore.catalog.domain.port.in.UpdateProductStatusResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
class ProductWebMapper {

    CreateProductCommand toCommand(CreateProductRequest request) {
        var variants = request.variants().stream()
                .map(v -> new CreateProductCommand.VariantInput(
                        v.attributeName(), v.attributeValue(),
                        v.price(), v.stock(), v.sku(),
                        v.weight(), v.width(), v.height(), v.depth()))
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
                        v.price(), v.stock(), v.sku(), v.active(), v.createdAt(),
                        v.weight(), v.width(), v.height(), v.depth()))
                .toList();

        return new ProductResponse(
                result.id(), result.name(), result.slug(), result.description(),
                result.categoryId(), result.status(), result.featured(),
                variants, result.createdAt()
        );
    }

    ProductImageResponse toImageResponse(ProductImageResult result) {
        return new ProductImageResponse(
                result.id(), result.url(), result.principal(),
                result.imageOrder(), result.createdAt()
        );
    }

    List<ProductImageResponse> toImageResponseList(List<ProductImageResult> results) {
        return results.stream().map(this::toImageResponse).toList();
    }

    ReorderProductImagesCommand toReorderCommand(UUID productId, ReorderImagesRequest request) {
        var items = request.items().stream()
                .map(i -> new ReorderProductImagesCommand.ImageOrderItem(i.imageId(), i.order()))
                .toList();
        return new ReorderProductImagesCommand(productId, items);
    }

    UpdateProductCommand toUpdateCommand(UUID id, UpdateProductRequest request) {
        return new UpdateProductCommand(
                ProductId.of(id),
                request.name(),
                request.description(),
                request.categoryId(),
                Boolean.TRUE.equals(request.featured())
        );
    }

    PatchProductCommand toPatchCommand(UUID id, PatchProductRequest request) {
        return new PatchProductCommand(
                ProductId.of(id),
                request.name(),
                request.description(),
                request.categoryId(),
                request.featured()
        );
    }

    ProductResponse toResponse(UpdateProductResult result) {
        var variants = result.variants().stream()
                .map(v -> new ProductResponse.VariantResponse(
                        v.id(), v.attributeName(), v.attributeValue(),
                        v.price(), v.stock(), v.sku(), v.active(), v.createdAt(),
                        v.weight(), v.width(), v.height(), v.depth()))
                .toList();

        return new ProductResponse(
                result.id(), result.name(), result.slug(), result.description(),
                result.categoryId(), result.status(), result.featured(),
                variants, result.createdAt()
        );
    }

    UpdateProductStatusResponse toStatusResponse(UpdateProductStatusResult result) {
        return new UpdateProductStatusResponse(result.id(), result.name(), result.status());
    }
}
