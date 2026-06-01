package br.com.miriageekstore.identity.infrastructure.adapter.out.order;

import br.com.miriageekstore.identity.domain.port.out.ActiveOrderChecker;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Placeholder until the Order module is implemented.
 * Always returns false — no active orders to block address deletion.
 */
@Component
class NoOpActiveOrderChecker implements ActiveOrderChecker {

    @Override
    public boolean hasActiveOrder(UUID addressId) {
        return false;
    }
}
