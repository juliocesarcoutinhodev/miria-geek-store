package br.com.miriageekstore.cart.application.usecase;

import br.com.miriageekstore.cart.domain.exception.CartInsufficientStockException;
import br.com.miriageekstore.cart.domain.exception.CartItemNotFoundException;
import br.com.miriageekstore.cart.domain.exception.CartItemNotOwnedException;
import br.com.miriageekstore.cart.domain.model.Cart;
import br.com.miriageekstore.cart.domain.model.CartId;
import br.com.miriageekstore.cart.domain.model.CartItem;
import br.com.miriageekstore.cart.domain.model.CartItemId;
import br.com.miriageekstore.cart.domain.port.in.UpdateCartItemCommand;
import br.com.miriageekstore.cart.domain.port.out.CartRepository;
import br.com.miriageekstore.cart.domain.port.out.CartVariantRepository;
import br.com.miriageekstore.cart.domain.port.out.CartVariantView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateCartItemUseCaseTest {

    @Mock CartRepository cartRepository;
    @Mock CartVariantRepository variantRepository;
    @InjectMocks UpdateCartItemUseCaseImpl useCase;

    @Test
    void shouldUpdateQuantityAndRefreshPriceSnapshot() {
        var userId    = UUID.randomUUID();
        var variantId = UUID.randomUUID();
        var itemId    = UUID.randomUUID();
        var cart      = cartWithItem(userId, itemId, variantId, 2, BigDecimal.valueOf(50.00));
        var variant   = variantView(variantId, BigDecimal.valueOf(60.00), 10);
        var command   = new UpdateCartItemCommand(userId, itemId, 3);

        when(cartRepository.findByItemId(itemId)).thenReturn(Optional.of(cart));
        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));
        when(cartRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(variantRepository.findAllByIds(any())).thenReturn(List.of(variant));

        var result = useCase.execute(command);

        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).quantity()).isEqualTo(3);
        assertThat(result.items().get(0).priceSnapshot()).isEqualByComparingTo(BigDecimal.valueOf(60.00));
        assertThat(result.subtotal()).isEqualByComparingTo(BigDecimal.valueOf(180.00));
    }

    @Test
    void shouldRemoveItemWhenQuantityIsZero() {
        var userId    = UUID.randomUUID();
        var variantId = UUID.randomUUID();
        var itemId    = UUID.randomUUID();
        var cart      = cartWithItem(userId, itemId, variantId, 2, BigDecimal.valueOf(50.00));
        var command   = new UpdateCartItemCommand(userId, itemId, 0);

        when(cartRepository.findByItemId(itemId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(variantRepository.findAllByIds(any())).thenReturn(List.of());

        var result = useCase.execute(command);

        assertThat(result.items()).isEmpty();
        assertThat(result.subtotal()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.itemCount()).isZero();
    }

    @Test
    void shouldThrowWhenItemNotFound() {
        var command = new UpdateCartItemCommand(UUID.randomUUID(), UUID.randomUUID(), 1);
        when(cartRepository.findByItemId(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(CartItemNotFoundException.class);
    }

    @Test
    void shouldThrowWhenItemBelongsToAnotherUser() {
        var anotherUserId = UUID.randomUUID();
        var itemId        = UUID.randomUUID();
        var cart          = cartWithItem(UUID.randomUUID(), itemId, UUID.randomUUID(), 1, BigDecimal.TEN);
        var command       = new UpdateCartItemCommand(anotherUserId, itemId, 2);

        when(cartRepository.findByItemId(itemId)).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(CartItemNotOwnedException.class);
    }

    @Test
    void shouldThrowWhenQuantityExceedsStock() {
        var userId    = UUID.randomUUID();
        var variantId = UUID.randomUUID();
        var itemId    = UUID.randomUUID();
        var cart      = cartWithItem(userId, itemId, variantId, 1, BigDecimal.valueOf(30.00));
        var variant   = variantView(variantId, BigDecimal.valueOf(30.00), 3);
        var command   = new UpdateCartItemCommand(userId, itemId, 5);

        when(cartRepository.findByItemId(itemId)).thenReturn(Optional.of(cart));
        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(CartInsufficientStockException.class)
                .hasMessageContaining("3");
    }

    @Test
    void shouldClearShippingOptionAfterUpdate() {
        var userId    = UUID.randomUUID();
        var variantId = UUID.randomUUID();
        var itemId    = UUID.randomUUID();
        var cart      = cartWithItemAndShipping(userId, itemId, variantId, 1, BigDecimal.valueOf(20.00));
        var variant   = variantView(variantId, BigDecimal.valueOf(20.00), 10);
        var command   = new UpdateCartItemCommand(userId, itemId, 2);

        when(cartRepository.findByItemId(itemId)).thenReturn(Optional.of(cart));
        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));
        when(cartRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(variantRepository.findAllByIds(any())).thenReturn(List.of(variant));

        useCase.execute(command);

        assertThat(cart.getSelectedShippingOptionId()).isNull();
    }

    // ── helpers ────────────────────────────────────────────────────────────────

    private CartVariantView variantView(UUID variantId, BigDecimal price, int stock) {
        return new CartVariantView(
                variantId, UUID.randomUUID(), "Produto Teste",
                "Cor", "Preto", "SKU-001", null,
                price, stock, true, true, true,
                BigDecimal.valueOf(0.5), BigDecimal.valueOf(20.0),
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(15.0));
    }

    private Cart cartWithItem(UUID userId, UUID itemId, UUID variantId,
                              int quantity, BigDecimal price) {
        var item = CartItem.reconstitute(CartItemId.of(itemId), variantId, quantity, price, Instant.now());
        return Cart.reconstitute(CartId.generate(), userId, List.of(item), Instant.now(), Instant.now(), null);
    }

    private Cart cartWithItemAndShipping(UUID userId, UUID itemId, UUID variantId,
                                         int quantity, BigDecimal price) {
        var item = CartItem.reconstitute(CartItemId.of(itemId), variantId, quantity, price, Instant.now());
        return Cart.reconstitute(CartId.generate(), userId, List.of(item), Instant.now(), Instant.now(), "ship-123");
    }
}
