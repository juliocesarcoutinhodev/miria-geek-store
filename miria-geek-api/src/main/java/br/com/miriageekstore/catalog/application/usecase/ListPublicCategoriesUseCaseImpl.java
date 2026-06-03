package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.port.in.ListPublicCategoriesResult;
import br.com.miriageekstore.catalog.domain.port.in.ListPublicCategoriesUseCase;
import br.com.miriageekstore.catalog.domain.port.out.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListPublicCategoriesUseCaseImpl implements ListPublicCategoriesUseCase {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public ListPublicCategoriesResult execute() {
        var categories = categoryRepository.findAllActive();

        var summaries = categories.stream()
                .map(c -> new ListPublicCategoriesResult.PublicCategorySummary(
                        c.getId().value(),
                        c.getName(),
                        c.getSlug().value(),
                        0L
                ))
                .toList();

        return new ListPublicCategoriesResult(summaries);
    }
}
