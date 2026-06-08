package br.com.miriageekstore.cart.domain.port.out;

import br.com.miriageekstore.cart.domain.model.Cart;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository {
    Optional<Cart> findByUserId(UUID userId);
    Cart save(Cart cart);
}
