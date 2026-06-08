package br.com.miriageekstore.cart.infrastructure.adapter.in.web;

import br.com.miriageekstore.cart.domain.port.in.CartItemResult;
import br.com.miriageekstore.cart.domain.port.in.CartResult;
import org.springframework.stereotype.Component;

@Component
class CartWebMapper {

    CartResponse toResponse(CartResult result) {
        var items    = result.items().stream().map(this::toItemResponse).toList();
        var shipping = result.selectedShipping() == null ? null
                : new CartResponse.SelectedShippingResponse(
                        result.selectedShipping().id(),
                        result.selectedShipping().name(),
                        result.selectedShipping().carrier(),
                        result.selectedShipping().value(),
                        result.selectedShipping().deliveryDays());

        return new CartResponse(
                result.id(),
                result.userId(),
                items,
                shipping,
                result.subtotal(),
                result.freight(),
                result.total(),
                result.itemCount(),
                result.createdAt(),
                result.updatedAt());
    }

    private CartItemResponse toItemResponse(CartItemResult item) {
        return new CartItemResponse(
                item.id(),
                item.variantId(),
                item.productId(),
                item.productName(),
                item.attributeName(),
                item.attributeValue(),
                item.sku(),
                item.principalImageUrl(),
                item.quantity(),
                item.priceSnapshot(),
                item.currentPrice(),
                item.subtotal(),
                item.availableStock(),
                item.variantActive(),
                item.priceChanged(),
                item.insufficientStock(),
                item.weight(),
                item.width(),
                item.height(),
                item.depth(),
                item.addedAt());
    }
}
