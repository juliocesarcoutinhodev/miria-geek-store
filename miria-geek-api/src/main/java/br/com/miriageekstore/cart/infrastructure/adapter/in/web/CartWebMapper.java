package br.com.miriageekstore.cart.infrastructure.adapter.in.web;

import br.com.miriageekstore.cart.domain.port.in.CartItemResult;
import br.com.miriageekstore.cart.domain.port.in.CartResult;
import org.springframework.stereotype.Component;

@Component
class CartWebMapper {

    CartResponse toResponse(CartResult result) {
        var items = result.items().stream().map(this::toItemResponse).toList();
        return new CartResponse(
                result.id(),
                result.userId(),
                items,
                result.subtotal(),
                result.itemCount(),
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
                item.subtotal(),
                item.availableStock(),
                item.variantActive(),
                item.addedAt());
    }
}
