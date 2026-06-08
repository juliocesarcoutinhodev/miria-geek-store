package br.com.miriageekstore.cart.domain.port.in;

import java.util.UUID;

public interface GetCartUseCase {
    CartResult execute(UUID userId);
}
