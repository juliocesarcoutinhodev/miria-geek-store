package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.CategoryNameAlreadyExistsException;
import br.com.miriageekstore.catalog.domain.exception.CategoryNotFoundException;
import br.com.miriageekstore.catalog.domain.port.in.UpdateCategoryCommand;
import br.com.miriageekstore.catalog.domain.port.in.UpdateCategoryResult;
import br.com.miriageekstore.catalog.domain.port.in.UpdateCategoryUseCase;
import br.com.miriageekstore.catalog.domain.port.out.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateCategoryUseCaseImpl implements UpdateCategoryUseCase {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public UpdateCategoryResult execute(UpdateCategoryCommand command) {
        var category = categoryRepository.findById(command.id())
                .orElseThrow(CategoryNotFoundException::new);

        if (categoryRepository.existsByNameExcludingId(command.name(), command.id())) {
            throw new CategoryNameAlreadyExistsException(command.name());
        }

        category.update(command.name(), command.description(), command.active());
        var saved = categoryRepository.save(category);

        return new UpdateCategoryResult(
                saved.getId().value(),
                saved.getName(),
                saved.getSlug().value(),
                saved.getDescription(),
                0L,
                saved.isActive(),
                saved.getCreatedAt()
        );
    }
}
