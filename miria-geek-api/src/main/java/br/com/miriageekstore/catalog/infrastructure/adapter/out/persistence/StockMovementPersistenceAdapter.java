package br.com.miriageekstore.catalog.infrastructure.adapter.out.persistence;

import br.com.miriageekstore.catalog.domain.model.StockMovement;
import br.com.miriageekstore.catalog.domain.port.out.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class StockMovementPersistenceAdapter implements StockMovementRepository {

    private final StockMovementJpaRepository jpaRepository;

    @Override
    public void save(StockMovement movement) {
        var entity = new StockMovementEntity();
        entity.setId(movement.id());
        entity.setVariantId(movement.variantId());
        entity.setProductId(movement.productId());
        entity.setType(movement.type());
        entity.setQuantity(movement.quantity());
        entity.setMotivo(movement.motivo());
        entity.setAdminId(movement.adminId());
        entity.setCreatedAt(movement.createdAt());
        jpaRepository.save(entity);
    }
}
