package br.com.miriageekstore.cart.application.usecase;

import br.com.miriageekstore.cart.domain.port.in.CartResult;
import br.com.miriageekstore.cart.domain.port.in.GetCartUseCase;
import br.com.miriageekstore.cart.domain.port.out.CartRepository;
import br.com.miriageekstore.cart.domain.port.out.CartVariantRepository;
import br.com.miriageekstore.cart.domain.port.out.CartVariantView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetCartUseCaseImpl implements GetCartUseCase {

    private final CartRepository cartRepository;
    private final CartVariantRepository variantRepository;

    @Override
    @Transactional(readOnly = true)
    public CartResult execute(UUID userId) {
        var cart = cartRepository.findByUserId(userId);
        if (cart.isEmpty()) {
            return CartResultAssembler.empty(userId);
        }

        var variantIds = cart.get().getItems().stream()
                .map(br.com.miriageekstore.cart.domain.model.CartItem::getVariantId)
                .toList();

        var variantMap = variantRepository.findAllByIds(variantIds).stream()
                .collect(Collectors.toMap(CartVariantView::variantId, v -> v));

        return CartResultAssembler.build(cart.get(), variantMap);
    }
}
