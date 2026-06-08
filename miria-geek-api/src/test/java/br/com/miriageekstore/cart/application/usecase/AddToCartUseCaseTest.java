package br.com.miriageekstore.cart.application.usecase;

import br.com.miriageekstore.cart.domain.exception.CartInsufficientStockException;
import br.com.miriageekstore.cart.domain.exception.CartVariantNotFoundException;
import br.com.miriageekstore.cart.domain.exception.ProductInactiveException;
import br.com.miriageekstore.cart.domain.exception.VariantInactiveException;
import br.com.miriageekstore.cart.domain.exception.VariantMissingDimensionsException;
import br.com.miriageekstore.cart.domain.model.Cart;
import br.com.miriageekstore.cart.domain.model.CartId;
import br.com.miriageekstore.cart.domain.model.CartItem;
import br.com.miriageekstore.cart.domain.model.CartItemId;
import br.com.miriageekstore.cart.domain.port.in.AddToCartCommand;
import br.com.miriageekstore.cart.domain.port.out.CartRepository;
import br.com.miriageekstore.cart.domain.port.out.CartVariantRepository;
import br.com.miriageekstore.cart.domain.port.out.CartVariantView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddToCartUseCaseTest {

    @Mock CartRepository cartRepository;
    @Mock CartVariantRepository variantRepository;
    @InjectMocks AddToCartUseCaseImpl useCase;

    @Test
    void shouldCreateNewCartAndAddItemWhenCartDoesNotExist() {
        var userId    = UUID.randomUUID();
        var variantId = UUID.randomUUID();
        var variant   = activeVariant(variantId, BigDecimal.valueOf(99.90), 10);
        var command   = new AddToCartCommand(userId, variantId, 2);

        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(cartRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(variantRepository.findAllByIds(any())).thenReturn(List.of(variant));

        var result = useCase.execute(command);

        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).quantity()).isEqualTo(2);
        assertThat(result.items().get(0).priceSnapshot()).isEqualByComparingTo(BigDecimal.valueOf(99.90));
        assertThat(result.subtotal()).isEqualByComparingTo(BigDecimal.valueOf(199.80));
        assertThat(result.itemCount()).isEqualTo(2);
    }

    @Test
    void shouldAddItemToExistingCart() {
        var userId    = UUID.randomUUID();
        var variantId = UUID.randomUUID();
        var variant   = activeVariant(variantId, BigDecimal.valueOf(50.00), 5);
        var cart      = emptyCart(userId);
        var command   = new AddToCartCommand(userId, variantId, 1);

        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(variantRepository.findAllByIds(any())).thenReturn(List.of(variant));

        var result = useCase.execute(command);

        assertThat(result.id()).isEqualTo(cart.getId().value());
        assertThat(result.items()).hasSize(1);
    }

    @Test
    void shouldMergeQuantityWhenVariantAlreadyInCart() {
        var userId    = UUID.randomUUID();
        var variantId = UUID.randomUUID();
        var variant   = activeVariant(variantId, BigDecimal.valueOf(30.00), 10);
        var cart      = cartWithItem(userId, variantId, 3, BigDecimal.valueOf(30.00));
        var command   = new AddToCartCommand(userId, variantId, 2);

        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(variantRepository.findAllByIds(any())).thenReturn(List.of(variant));

        var result = useCase.execute(command);

        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).quantity()).isEqualTo(5);
    }

    @Test
    void shouldClearShippingOptionWhenItemAdded() {
        var userId    = UUID.randomUUID();
        var variantId = UUID.randomUUID();
        var variant   = activeVariant(variantId, BigDecimal.valueOf(10.00), 5);
        var command   = new AddToCartCommand(userId, variantId, 1);

        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        var captor = ArgumentCaptor.forClass(Cart.class);
        when(cartRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
        when(variantRepository.findAllByIds(any())).thenReturn(List.of(variant));

        useCase.execute(command);

        assertThat(captor.getValue().getSelectedShippingOptionId()).isNull();
    }

    @Test
    void shouldThrowWhenVariantNotFound() {
        var command = new AddToCartCommand(UUID.randomUUID(), UUID.randomUUID(), 1);
        when(variantRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(CartVariantNotFoundException.class);
    }

    @Test
    void shouldThrowWhenVariantIsInactive() {
        var variantId = UUID.randomUUID();
        var variant   = variantView(variantId, 10, false, true, true);
        var command   = new AddToCartCommand(UUID.randomUUID(), variantId, 1);

        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(VariantInactiveException.class);
    }

    @Test
    void shouldThrowWhenProductIsInactive() {
        var variantId = UUID.randomUUID();
        var variant   = variantView(variantId, 10, true, false, true);
        var command   = new AddToCartCommand(UUID.randomUUID(), variantId, 1);

        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(ProductInactiveException.class);
    }

    @Test
    void shouldThrowWhenVariantMissingDimensions() {
        var variantId = UUID.randomUUID();
        var variant   = variantView(variantId, 10, true, true, false);
        var command   = new AddToCartCommand(UUID.randomUUID(), variantId, 1);

        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(VariantMissingDimensionsException.class)
                .hasMessageContaining("dimensões");
    }

    @Test
    void shouldThrowWhenRequestedQuantityExceedsStock() {
        var userId    = UUID.randomUUID();
        var variantId = UUID.randomUUID();
        var variant   = activeVariant(variantId, BigDecimal.valueOf(20.00), 3);
        var command   = new AddToCartCommand(userId, variantId, 5);

        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(CartInsufficientStockException.class)
                .hasMessageContaining("3");
    }

    @Test
    void shouldThrowWhenCartQuantityPlusRequestExceedsStock() {
        var userId    = UUID.randomUUID();
        var variantId = UUID.randomUUID();
        var variant   = activeVariant(variantId, BigDecimal.valueOf(20.00), 4);
        var cart      = cartWithItem(userId, variantId, 3, BigDecimal.valueOf(20.00));
        var command   = new AddToCartCommand(userId, variantId, 2);

        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(CartInsufficientStockException.class)
                .hasMessageContaining("4");
    }

    // ── helpers ────────────────────────────────────────────────────────────────

    private CartVariantView activeVariant(UUID variantId, BigDecimal price, int stock) {
        return new CartVariantView(
                variantId, UUID.randomUUID(), "Produto Teste",
                "Cor", "Preto", "SKU-001", null,
                price, stock, true, true, true,
                java.math.BigDecimal.valueOf(0.5), java.math.BigDecimal.valueOf(20.0),
                java.math.BigDecimal.valueOf(10.0), java.math.BigDecimal.valueOf(15.0));
    }

    private CartVariantView variantView(UUID variantId, int stock,
                                        boolean variantActive, boolean productActive, boolean hasDimensions) {
        return new CartVariantView(
                variantId, UUID.randomUUID(), "Produto Teste",
                "Cor", "Preto", "SKU-001", null,
                BigDecimal.TEN, stock, variantActive, productActive, hasDimensions,
                null, null, null, null);
    }

    private Cart emptyCart(UUID userId) {
        return Cart.reconstitute(CartId.generate(), userId, List.of(), Instant.now(), Instant.now(), null);
    }

    private Cart cartWithItem(UUID userId, UUID variantId, int quantity, BigDecimal price) {
        var item = CartItem.reconstitute(
                CartItemId.generate(), variantId, quantity, price, Instant.now());
        return Cart.reconstitute(CartId.generate(), userId, List.of(item), Instant.now(), Instant.now(), "shipping-123");
    }
}
