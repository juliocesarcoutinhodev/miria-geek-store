package br.com.miriageekstore.catalog.domain.port.out;

import br.com.miriageekstore.catalog.domain.model.StockMovement;

public interface StockMovementRepository {
    void save(StockMovement movement);
}
