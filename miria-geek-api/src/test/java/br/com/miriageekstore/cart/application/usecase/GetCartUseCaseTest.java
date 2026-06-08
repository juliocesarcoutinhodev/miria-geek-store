package br.com.miriageekstore.cart.application.usecase;

import br.com.miriageekstore.cart.domain.model.Cart;
import br.com.miriageekstore.cart.domain.model.CartId;
import br.com.miriageekstore.cart.domain.model.CartItem;
import br.com.miriageekstore.cart.domain.model.CartItemId;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCartUseCaseTest {

    @Mock CartRepository cartRepository;
    @Mock CartVariantRepository variantRepository;
    @InjectMocks GetCartUseCaseImpl useCase;

    @Test
    void shouldReturnEmptyCartWhenUserHasNoCart() {
        var userId = UUID.randomUUID();
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        var result = useCase.execute(userId);

        assertThat(result.id()).isNull();
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.items()).isEmpty();
        assertThat(result.subtotal()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.freight()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.total()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.itemCount()).isZero();
        assertThat(result.selectedShipping()).isNull();
    }

    @Test
    void shouldReturnCartWithItemsAndCorrectTotals() {
        var userId    = UUID.randomUUID();
        var variantId = UUID.randomUUID();
        var cart      = cartWithItem(userId, variantId, 2, BigDecimal.valueOf(50.00));
        var variant   = variantView(variantId, BigDecimal.valueOf(50.00), 10);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(variantRepository.findAllByIds(any())).thenReturn(List.of(variant));

        var result = useCase.execute(userId);

        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).quantity()).isEqualTo(2);
        assertThat(result.items().get(0).priceSnapshot()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
        assertThat(result.subtotal()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
        assertThat(result.freight()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.total()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
        assertThat(result.itemCount()).isEqualTo(2);
    }

    @Test
    void shouldSetPriceChangedWhenCurrentPriceDiffersFromSnapshot() {
        var userId    = UUID.randomUUID();
        var variantId = UUID.randomUUID();
        var cart      = cartWithItem(userId, variantId, 1, BigDecimal.valueOf(80.00));
        var variant   = variantView(variantId, BigDecimal.valueOf(99.00), 5);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(variantRepository.findAllByIds(any())).thenReturn(List.of(variant));

        var result = useCase.execute(userId);

        assertThat(result.items().get(0).priceChanged()).isTrue();
        assertThat(result.items().get(0).currentPrice()).isEqualByComparingTo(BigDecimal.valueOf(99.00));
    }

    @Test
    void shouldNotSetPriceChangedWhenCurrentPriceMatchesSnapshot() {
        var userId    = UUID.randomUUID();
        var variantId = UUID.randomUUID();
        var cart      = cartWithItem(userId, variantId, 1, BigDecimal.valueOf(50.00));
        var variant   = variantView(variantId, BigDecimal.valueOf(50.00), 5);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(variantRepository.findAllByIds(any())).thenReturn(List.of(variant));

        var result = useCase.execute(userId);

        assertThat(result.items().get(0).priceChanged()).isFalse();
    }

    @Test
    void shouldSetInsufficientStockWhenAvailableStockLessThanCartQuantity() {
        var userId    = UUID.randomUUID();
        var variantId = UUID.randomUUID();
        var cart      = cartWithItem(userId, variantId, 5, BigDecimal.valueOf(30.00));
        var variant   = variantView(variantId, BigDecimal.valueOf(30.00), 3);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(variantRepository.findAllByIds(any())).thenReturn(List.of(variant));

        var result = useCase.execute(userId);

        assertThat(result.items().get(0).insufficientStock()).isTrue();
        assertThat(result.items().get(0).availableStock()).isEqualTo(3);
    }

    @Test
    void shouldReturnDimensionsFromVariantView() {
        var userId    = UUID.randomUUID();
        var variantId = UUID.randomUUID();
        var cart      = cartWithItem(userId, variantId, 1, BigDecimal.valueOf(40.00));
        var variant   = new CartVariantView(
                variantId, UUID.randomUUID(), "Produto", "Cor", "Azul", "SKU-X", null,
                BigDecimal.valueOf(40.00), 10, true, true, true,
                BigDecimal.valueOf(0.8), BigDecimal.valueOf(25.0),
                BigDecimal.valueOf(12.0), BigDecimal.valueOf(18.0));

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(variantRepository.findAllByIds(any())).thenReturn(List.of(variant));

        var result = useCase.execute(userId);
        var item   = result.items().get(0);

        assertThat(item.weight()).isEqualByComparingTo(BigDecimal.valueOf(0.8));
        assertThat(item.width()).isEqualByComparingTo(BigDecimal.valueOf(25.0));
        assertThat(item.height()).isEqualByComparingTo(BigDecimal.valueOf(12.0));
        assertThat(item.depth()).isEqualByComparingTo(BigDecimal.valueOf(18.0));
    }

    @Test
    void shouldAlwaysReturnNullSelectedShipping() {
        var userId = UUID.randomUUID();
        var cart   = emptyCartWithShippingId(userId, "ship-option-1");

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(variantRepository.findAllByIds(any())).thenReturn(List.of());

        var result = useCase.execute(userId);

        assertThat(result.selectedShipping()).isNull();
        assertThat(result.freight()).isEqualByComparingTo(BigDecimal.ZERO);
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

    private Cart cartWithItem(UUID userId, UUID variantId, int quantity, BigDecimal price) {
        var item = CartItem.reconstitute(CartItemId.generate(), variantId, quantity, price, Instant.now());
        return Cart.reconstitute(CartId.generate(), userId, List.of(item), Instant.now(), Instant.now(), null);
    }

    private Cart emptyCartWithShippingId(UUID userId, String shippingId) {
        return Cart.reconstitute(CartId.generate(), userId, List.of(), Instant.now(), Instant.now(), shippingId);
    }
}
