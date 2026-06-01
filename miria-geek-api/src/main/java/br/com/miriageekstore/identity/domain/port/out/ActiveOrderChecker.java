package br.com.miriageekstore.identity.domain.port.out;

import java.util.UUID;

public interface ActiveOrderChecker {
    boolean hasActiveOrder(UUID addressId);
}
