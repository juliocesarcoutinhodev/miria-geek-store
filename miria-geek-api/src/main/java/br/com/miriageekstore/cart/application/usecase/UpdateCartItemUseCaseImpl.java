package br.com.miriageekstore.cart.application.usecase;

import br.com.miriageekstore.cart.domain.exception.CartInsufficientStockException;
import br.com.miriageekstore.cart.domain.exception.CartItemNotFoundException;
import br.com.miriageekstore.cart.domain.exception.CartItemNotOwnedException;
import br.com.miriageekstore.cart.domain.exception.CartVariantNotFoundException;
import br.com.miriageekstore.cart.domain.model.CartItem;
import br.com.miriageekstore.cart.domain.port.in.CartResult;
import br.com.miriageekstore.cart.domain.port.in.UpdateCartItemCommand;
import br.com.miriageekstore.cart.domain.port.in.UpdateCartItemUseCase;
import br.com.miriageekstore.cart.domain.port.out.CartRepository;
import br.com.miriageekstore.cart.domain.port.out.CartVariantRepository;
import br.com.miriageekstore.cart.domain.port.out.CartVariantView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UpdateCartItemUseCaseImpl implements UpdateCartItemUseCase {

    private final CartRepository cartRepository;
    private final CartVariantRepository variantRepository;

    @Override
    @Transactional
    public CartResult execute(UpdateCartItemCommand command) {
        var cart = cartRepository.findByItemId(command.itemId())
                .orElseThrow(CartItemNotFoundException::new);

        if (!cart.getUserId().equals(command.userId())) {
            throw new CartItemNotOwnedException();
        }

        if (command.quantity() > 0) {
            var item = cart.findItemById(command.itemId()).orElseThrow(CartItemNotFoundException::new);
            var variant = variantRepository.findById(item.getVariantId())
                    .orElseThrow(CartVariantNotFoundException::new);

            if (command.quantity() > variant.stock()) {
                throw new CartInsufficientStockException(variant.stock());
            }

            cart.updateItemQuantity(command.itemId(), command.quantity(), variant.price());
        } else {
            cart.updateItemQuantity(command.itemId(), 0, null);
        }

        var saved = cartRepository.save(cart);

        var variantIds = saved.getItems().stream()
                .map(CartItem::getVariantId).toList();
        var variantMap = variantRepository.findAllByIds(variantIds).stream()
                .collect(Collectors.toMap(CartVariantView::variantId, v -> v));

        return CartResultAssembler.build(saved, variantMap);
    }
}
