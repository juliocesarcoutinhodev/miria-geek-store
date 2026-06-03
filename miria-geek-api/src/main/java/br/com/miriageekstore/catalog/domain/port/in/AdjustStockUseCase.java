package br.com.miriageekstore.catalog.domain.port.in;

public interface AdjustStockUseCase {
    AdjustStockResult execute(AdjustStockCommand command);
}
