package br.com.miriageekstore.catalog.infrastructure.adapter.in.web;

import br.com.miriageekstore.catalog.domain.port.in.AddVariantCommand;
import br.com.miriageekstore.catalog.domain.port.in.AddVariantResult;
import br.com.miriageekstore.catalog.domain.port.in.AdjustStockCommand;
import br.com.miriageekstore.catalog.domain.port.in.AdjustStockResult;
import br.com.miriageekstore.catalog.domain.port.in.ListVariantsResult;
import br.com.miriageekstore.catalog.domain.port.in.UpdateVariantCommand;
import br.com.miriageekstore.catalog.domain.port.in.UpdateVariantResult;
import br.com.miriageekstore.catalog.domain.port.in.UpdateVariantStatusCommand;
import br.com.miriageekstore.catalog.domain.port.in.UpdateVariantStatusResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
class VariantWebMapper {

    AddVariantCommand toAddCommand(UUID productId, AddVariantRequest request) {
        return new AddVariantCommand(
                productId,
                request.attributeName(),
                request.attributeValue(),
                request.price(),
                request.stock(),
                request.sku(),
                request.weight(),
                request.width(),
                request.height(),
                request.depth()
        );
    }

    VariantResponse toResponse(AddVariantResult result) {
        return new VariantResponse(
                result.id(), result.attributeName(), result.attributeValue(),
                result.price(), result.stock(), result.sku(), result.active(), result.createdAt(),
                result.weight(), result.width(), result.height(), result.depth()
        );
    }

    List<VariantResponse> toResponseList(ListVariantsResult result) {
        return result.variants().stream()
                .map(v -> new VariantResponse(
                        v.id(), v.attributeName(), v.attributeValue(),
                        v.price(), v.stock(), v.sku(), v.active(), v.createdAt(),
                        v.weight(), v.width(), v.height(), v.depth()))
                .toList();
    }

    UpdateVariantCommand toUpdateCommand(UUID productId, UUID variantId, UpdateVariantRequest request) {
        return new UpdateVariantCommand(
                productId,
                variantId,
                request.attributeName(),
                request.attributeValue(),
                request.price(),
                request.stock(),
                request.sku(),
                request.weight(),
                request.width(),
                request.height(),
                request.depth()
        );
    }

    VariantResponse toResponse(UpdateVariantResult result) {
        return new VariantResponse(
                result.id(), result.attributeName(), result.attributeValue(),
                result.price(), result.stock(), result.sku(), result.active(), result.createdAt(),
                result.weight(), result.width(), result.height(), result.depth()
        );
    }

    UpdateVariantStatusCommand toStatusCommand(UUID productId, UUID variantId, UpdateVariantStatusRequest request) {
        return new UpdateVariantStatusCommand(productId, variantId, request.ativo());
    }

    VariantResponse toResponse(UpdateVariantStatusResult result) {
        return new VariantResponse(
                result.id(), result.attributeName(), result.attributeValue(),
                result.price(), result.stock(), result.sku(), result.active(), result.createdAt(),
                result.weight(), result.width(), result.height(), result.depth()
        );
    }

    AdjustStockCommand toAdjustCommand(UUID productId, UUID variantId, AdjustStockRequest request, UUID adminId) {
        return new AdjustStockCommand(
                productId,
                variantId,
                request.tipo(),
                request.quantidade(),
                request.motivo(),
                adminId
        );
    }

    AdjustStockResponse toResponse(AdjustStockResult result) {
        return new AdjustStockResponse(result.variantId(), result.sku(), result.stock());
    }
}
