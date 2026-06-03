package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.port.in.ListCategoriesQuery;
import br.com.miriageekstore.catalog.domain.port.in.ListCategoriesResult;
import br.com.miriageekstore.catalog.domain.port.in.ListCategoriesUseCase;
import br.com.miriageekstore.catalog.domain.port.out.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListCategoriesUseCaseImpl implements ListCategoriesUseCase {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public ListCategoriesResult execute(ListCategoriesQuery query) {
        var page = categoryRepository.findAll(
                query.name(), query.active(),
                query.page(), query.size(),
                query.sort(), query.direction()
        );

        var content = page.content().stream()
                .map(c -> new ListCategoriesResult.CategorySummary(
                        c.getId().value(),
                        c.getName(),
                        c.getSlug().value(),
                        c.getDescription(),
                        0L,
                        c.isActive(),
                        c.getCreatedAt()
                ))
                .toList();

        return new ListCategoriesResult(
                content, page.page(), page.size(), page.totalElements(), page.totalPages());
    }
}
