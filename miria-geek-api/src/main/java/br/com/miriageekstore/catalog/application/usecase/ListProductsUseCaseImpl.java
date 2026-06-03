package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.port.in.ListProductsResult;
import br.com.miriageekstore.catalog.domain.port.in.ListProductsUseCase;
import br.com.miriageekstore.catalog.domain.port.in.ProductSearchQuery;
import br.com.miriageekstore.catalog.domain.port.out.ProductCatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListProductsUseCaseImpl implements ListProductsUseCase {

    private final ProductCatalogRepository repository;

    @Override
    @Transactional(readOnly = true)
    public ListProductsResult execute(ProductSearchQuery query) {
        return repository.search(query);
    }
}
