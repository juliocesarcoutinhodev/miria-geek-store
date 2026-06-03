package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.CategoryNotFoundException;
import br.com.miriageekstore.catalog.domain.model.CategoryId;
import br.com.miriageekstore.catalog.domain.port.in.GetCategoryByIdResult;
import br.com.miriageekstore.catalog.domain.port.in.GetCategoryByIdUseCase;
import br.com.miriageekstore.catalog.domain.port.out.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetCategoryByIdUseCaseImpl implements GetCategoryByIdUseCase {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public GetCategoryByIdResult execute(CategoryId id) {
        var category = categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);

        return new GetCategoryByIdResult(
                category.getId().value(),
                category.getName(),
                category.getSlug().value(),
                category.getDescription(),
                0L,
                category.isActive(),
                category.getCreatedAt()
        );
    }
}
