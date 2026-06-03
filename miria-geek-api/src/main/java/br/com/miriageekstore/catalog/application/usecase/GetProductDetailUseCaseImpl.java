package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.ProductNotFoundException;
import br.com.miriageekstore.catalog.domain.port.in.GetProductDetailResult;
import br.com.miriageekstore.catalog.domain.port.in.GetProductDetailUseCase;
import br.com.miriageekstore.catalog.domain.port.out.ProductCatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetProductDetailUseCaseImpl implements GetProductDetailUseCase {

    private final ProductCatalogRepository repository;

    @Override
    @Transactional(readOnly = true)
    public GetProductDetailResult execute(String slug) {
        return repository.findBySlug(slug)
                .orElseThrow(ProductNotFoundException::new);
    }
}
