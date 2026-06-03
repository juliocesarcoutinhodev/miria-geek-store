package br.com.miriageekstore.catalog.domain.port.in;

import java.util.UUID;

public interface ListVariantsUseCase {
    ListVariantsResult execute(UUID productId);
}
