package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.event.StockUpdated;
import br.com.miriageekstore.catalog.domain.exception.ProductNotFoundException;
import br.com.miriageekstore.catalog.domain.model.ProductId;
import br.com.miriageekstore.catalog.domain.model.StockMovement;
import br.com.miriageekstore.catalog.domain.model.VariantId;
import br.com.miriageekstore.catalog.domain.port.in.AdjustStockCommand;
import br.com.miriageekstore.catalog.domain.port.in.AdjustStockResult;
import br.com.miriageekstore.catalog.domain.port.in.AdjustStockUseCase;
import br.com.miriageekstore.catalog.domain.port.out.CatalogEventPublisher;
import br.com.miriageekstore.catalog.domain.port.out.ProductRepository;
import br.com.miriageekstore.catalog.domain.port.out.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AdjustStockUseCaseImpl implements AdjustStockUseCase {

    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;
    private final CatalogEventPublisher eventPublisher;

    @Override
    @Transactional
    public AdjustStockResult execute(AdjustStockCommand command) {
        var product = productRepository.findById(ProductId.of(command.productId()))
                .orElseThrow(ProductNotFoundException::new);

        var variantId = VariantId.of(command.variantId());
        product.adjustVariantStock(variantId, command.type(), command.quantity());
        productRepository.save(product);

        var variant = product.findVariantOrThrow(variantId);

        stockMovementRepository.save(StockMovement.create(
                command.variantId(), command.productId(), command.type(),
                command.quantity(), command.motivo(), command.adminId()));

        eventPublisher.publish(new StockUpdated(
                command.productId(), command.variantId(), variant.getSku().value(),
                command.type(), command.quantity(), command.motivo(),
                variant.getStock(), command.adminId(), Instant.now()));

        return new AdjustStockResult(variant.getId().value(), variant.getSku().value(), variant.getStock());
    }
}
