package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.ProductNotFoundException;
import br.com.miriageekstore.catalog.domain.port.in.GetAdminProductDetailResult;
import br.com.miriageekstore.catalog.domain.port.in.GetAdminProductDetailUseCase;
import br.com.miriageekstore.catalog.domain.port.out.ProductCatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetAdminProductDetailUseCaseImpl implements GetAdminProductDetailUseCase {

    private final ProductCatalogRepository repository;

    @Override
    @Transactional(readOnly = true)
    public GetAdminProductDetailResult execute(UUID productId) {
        return repository.findByIdForAdmin(productId)
                .orElseThrow(ProductNotFoundException::new);
    }
}
