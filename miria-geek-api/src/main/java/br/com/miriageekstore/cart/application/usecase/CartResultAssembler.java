package br.com.miriageekstore.cart.application.usecase;

import br.com.miriageekstore.cart.domain.model.Cart;
import br.com.miriageekstore.cart.domain.model.CartItem;
import br.com.miriageekstore.cart.domain.port.in.CartItemResult;
import br.com.miriageekstore.cart.domain.port.in.CartResult;
import br.com.miriageekstore.cart.domain.port.out.CartVariantView;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class CartResultAssembler {

    static CartResult build(Cart cart, Map<UUID, CartVariantView> variantMap) {
        List<CartItemResult> items = cart.getItems().stream()
                .map(item -> toItemResult(item, variantMap.get(item.getVariantId())))
                .toList();

        var subtotal  = items.stream().map(CartItemResult::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        int itemCount = items.stream().mapToInt(CartItemResult::quantity).sum();

        return new CartResult(
                cart.getId().value(),
                cart.getUserId(),
                items,
                null,
                subtotal,
                BigDecimal.ZERO,
                subtotal,
                itemCount,
                cart.getCreatedAt(),
                cart.getUpdatedAt());
    }

    static CartResult empty(UUID userId) {
        return new CartResult(
                null, userId, List.of(), null,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                0, null, null);
    }

    private static CartItemResult toItemResult(CartItem item, CartVariantView v) {
        var subtotal         = item.getPriceSnapshot().multiply(BigDecimal.valueOf(item.getQuantity()));
        boolean priceChanged = v != null && v.price().compareTo(item.getPriceSnapshot()) != 0;
        boolean lowStock     = v != null && v.stock() < item.getQuantity();

        return new CartItemResult(
                item.getId().value(),
                item.getVariantId(),
                v != null ? v.productId()        : null,
                v != null ? v.productName()       : null,
                v != null ? v.attributeName()     : null,
                v != null ? v.attributeValue()    : null,
                v != null ? v.sku()               : null,
                v != null ? v.principalImageUrl() : null,
                item.getQuantity(),
                item.getPriceSnapshot(),
                v != null ? v.price()             : null,
                subtotal,
                v != null ? v.stock()             : 0,
                v != null && v.variantActive(),
                priceChanged,
                lowStock,
                v != null ? v.weight()            : null,
                v != null ? v.width()             : null,
                v != null ? v.height()            : null,
                v != null ? v.depth()             : null,
                item.getAddedAt());
    }
}
