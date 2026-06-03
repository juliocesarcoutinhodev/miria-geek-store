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
                request.sku()
        );
    }

    VariantResponse toResponse(AddVariantResult result) {
        return new VariantResponse(
                result.id(), result.attributeName(), result.attributeValue(),
                result.price(), result.stock(), result.sku(), result.active(), result.createdAt()
        );
    }

    List<VariantResponse> toResponseList(ListVariantsResult result) {
        return result.variants().stream()
                .map(v -> new VariantResponse(
                        v.id(), v.attributeName(), v.attributeValue(),
                        v.price(), v.stock(), v.sku(), v.active(), v.createdAt()))
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
                request.sku()
        );
    }

    VariantResponse toResponse(UpdateVariantResult result) {
        return new VariantResponse(
                result.id(), result.attributeName(), result.attributeValue(),
                result.price(), result.stock(), result.sku(), result.active(), result.createdAt()
        );
    }

    UpdateVariantStatusCommand toStatusCommand(UUID productId, UUID variantId, UpdateVariantStatusRequest request) {
        return new UpdateVariantStatusCommand(productId, variantId, request.ativo());
    }

    VariantResponse toResponse(UpdateVariantStatusResult result) {
        return new VariantResponse(
                result.id(), result.attributeName(), result.attributeValue(),
                result.price(), result.stock(), result.sku(), result.active(), result.createdAt()
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
