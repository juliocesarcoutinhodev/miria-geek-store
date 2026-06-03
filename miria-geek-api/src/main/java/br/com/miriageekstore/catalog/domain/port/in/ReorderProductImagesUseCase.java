package br.com.miriageekstore.catalog.domain.port.in;

import java.util.List;

public interface ReorderProductImagesUseCase {
    List<ProductImageResult> execute(ReorderProductImagesCommand command);
}
