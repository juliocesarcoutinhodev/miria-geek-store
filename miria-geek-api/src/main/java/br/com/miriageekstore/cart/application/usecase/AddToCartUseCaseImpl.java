package br.com.miriageekstore.cart.application.usecase;

import br.com.miriageekstore.cart.domain.exception.CartInsufficientStockException;
import br.com.miriageekstore.cart.domain.exception.CartVariantNotFoundException;
import br.com.miriageekstore.cart.domain.exception.ProductInactiveException;
import br.com.miriageekstore.cart.domain.exception.VariantInactiveException;
import br.com.miriageekstore.cart.domain.exception.VariantMissingDimensionsException;
import br.com.miriageekstore.cart.domain.model.Cart;
import br.com.miriageekstore.cart.domain.port.in.AddToCartCommand;
import br.com.miriageekstore.cart.domain.port.in.AddToCartUseCase;
import br.com.miriageekstore.cart.domain.port.in.CartItemResult;
import br.com.miriageekstore.cart.domain.port.in.CartResult;
import br.com.miriageekstore.cart.domain.port.out.CartRepository;
import br.com.miriageekstore.cart.domain.port.out.CartVariantRepository;
import br.com.miriageekstore.cart.domain.port.out.CartVariantView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddToCartUseCaseImpl implements AddToCartUseCase {

    private final CartRepository cartRepository;
    private final CartVariantRepository variantRepository;

    @Override
    @Transactional
    public CartResult execute(AddToCartCommand command) {
        var variant = variantRepository.findById(command.variantId())
                .orElseThrow(CartVariantNotFoundException::new);

        if (!variant.variantActive()) throw new VariantInactiveException();
        if (!variant.productActive()) throw new ProductInactiveException();
        if (!variant.hasDimensions()) throw new VariantMissingDimensionsException();

        var cart = cartRepository.findByUserId(command.userId())
                .orElseGet(() -> Cart.create(command.userId()));

        int alreadyInCart = cart.findItemByVariantId(command.variantId())
                .map(br.com.miriageekstore.cart.domain.model.CartItem::getQuantity)
                .orElse(0);

        if (alreadyInCart + command.quantity() > variant.stock()) {
            throw new CartInsufficientStockException(variant.stock());
        }

        cart.addItem(command.variantId(), command.quantity(), variant.price());

        var saved = cartRepository.save(cart);

        var variantIds = saved.getItems().stream()
                .map(br.com.miriageekstore.cart.domain.model.CartItem::getVariantId)
                .toList();
        var variantMap = variantRepository.findAllByIds(variantIds).stream()
                .collect(Collectors.toMap(CartVariantView::variantId, v -> v));

        return buildResult(saved, variantMap);
    }

    private CartResult buildResult(Cart cart, Map<UUID, CartVariantView> variantMap) {
        List<CartItemResult> items = cart.getItems().stream()
                .map(item -> {
                    var v = variantMap.get(item.getVariantId());
                    var subtotal = item.getPriceSnapshot()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));
                    return new CartItemResult(
                            item.getId().value(),
                            item.getVariantId(),
                            v != null ? v.productId()         : null,
                            v != null ? v.productName()        : null,
                            v != null ? v.attributeName()      : null,
                            v != null ? v.attributeValue()     : null,
                            v != null ? v.sku()                : null,
                            v != null ? v.principalImageUrl()  : null,
                            item.getQuantity(),
                            item.getPriceSnapshot(),
                            subtotal,
                            v != null ? v.stock()              : 0,
                            v != null && v.variantActive(),
                            item.getAddedAt()
                    );
                })
                .toList();

        var subtotal = items.stream()
                .map(CartItemResult::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int itemCount = items.stream().mapToInt(CartItemResult::quantity).sum();

        return new CartResult(
                cart.getId().value(),
                cart.getUserId(),
                items,
                subtotal,
                itemCount,
                cart.getUpdatedAt()
        );
    }
}
