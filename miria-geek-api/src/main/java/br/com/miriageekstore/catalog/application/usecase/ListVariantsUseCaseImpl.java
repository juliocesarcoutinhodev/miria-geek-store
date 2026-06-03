package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.ProductNotFoundException;
import br.com.miriageekstore.catalog.domain.model.ProductId;
import br.com.miriageekstore.catalog.domain.port.in.ListVariantsResult;
import br.com.miriageekstore.catalog.domain.port.in.ListVariantsUseCase;
import br.com.miriageekstore.catalog.domain.port.out.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListVariantsUseCaseImpl implements ListVariantsUseCase {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public ListVariantsResult execute(UUID productId) {
        var product = productRepository.findById(ProductId.of(productId))
                .orElseThrow(ProductNotFoundException::new);

        var items = product.getVariants().stream()
                .map(v -> new ListVariantsResult.VariantItem(
                        v.getId().value(), v.getAttributeName(), v.getAttributeValue(),
                        v.getPrice(), v.getStock(), v.getSku().value(), v.isActive(), v.getCreatedAt()))
                .toList();

        return new ListVariantsResult(items);
    }
}
