package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.port.in.AdminProductQuery;
import br.com.miriageekstore.catalog.domain.port.in.ListAdminProductsResult;
import br.com.miriageekstore.catalog.domain.port.in.ListAdminProductsUseCase;
import br.com.miriageekstore.catalog.domain.port.out.ProductCatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListAdminProductsUseCaseImpl implements ListAdminProductsUseCase {

    private final ProductCatalogRepository repository;

    @Override
    @Transactional(readOnly = true)
    public ListAdminProductsResult execute(AdminProductQuery query) {
        return repository.searchForAdmin(query);
    }
}
