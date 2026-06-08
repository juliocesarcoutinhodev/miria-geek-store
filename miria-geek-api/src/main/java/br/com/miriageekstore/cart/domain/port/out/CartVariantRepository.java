package br.com.miriageekstore.cart.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartVariantRepository {
    Optional<CartVariantView> findById(UUID variantId);
    List<CartVariantView> findAllByIds(List<UUID> variantIds);
}
