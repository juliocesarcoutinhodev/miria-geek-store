package br.com.miriageekstore.cart.infrastructure.adapter.out.persistence;

import br.com.miriageekstore.cart.domain.model.Cart;
import br.com.miriageekstore.cart.domain.model.CartId;
import br.com.miriageekstore.cart.domain.model.CartItem;
import br.com.miriageekstore.cart.domain.model.CartItemId;
import br.com.miriageekstore.cart.domain.port.out.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class CartPersistenceAdapter implements CartRepository {

    private final CartJpaRepository jpaRepository;

    @Override
    public Optional<Cart> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public Cart save(Cart cart) {
        var entity = jpaRepository.findById(cart.getId().value())
                .orElseGet(() -> {
                    var e = new CartEntity();
                    e.setId(cart.getId().value());
                    return e;
                });

        entity.setUserId(cart.getUserId());
        entity.setUpdatedAt(cart.getUpdatedAt());
        entity.setSelectedShippingOptionId(cart.getSelectedShippingOptionId());

        entity.getItems().clear();
        for (var item : cart.getItems()) {
            var itemEntity = new CartItemEntity();
            itemEntity.setId(item.getId().value());
            itemEntity.setCart(entity);
            itemEntity.setVariantId(item.getVariantId());
            itemEntity.setQuantity(item.getQuantity());
            itemEntity.setPriceSnapshot(item.getPriceSnapshot());
            itemEntity.setAddedAt(item.getAddedAt());
            entity.getItems().add(itemEntity);
        }

        return toDomain(jpaRepository.save(entity));
    }

    private Cart toDomain(CartEntity e) {
        List<CartItem> items = e.getItems().stream()
                .map(i -> CartItem.reconstitute(
                        CartItemId.of(i.getId()),
                        i.getVariantId(),
                        i.getQuantity(),
                        i.getPriceSnapshot(),
                        i.getAddedAt()))
                .toList();
        return Cart.reconstitute(
                CartId.of(e.getId()),
                e.getUserId(),
                items,
                e.getUpdatedAt(),
                e.getSelectedShippingOptionId());
    }
}
