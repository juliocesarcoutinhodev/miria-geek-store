package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.CategoryNameAlreadyExistsException;
import br.com.miriageekstore.catalog.domain.model.Category;
import br.com.miriageekstore.catalog.domain.port.in.CreateCategoryCommand;
import br.com.miriageekstore.catalog.domain.port.in.CreateCategoryResult;
import br.com.miriageekstore.catalog.domain.port.in.CreateCategoryUseCase;
import br.com.miriageekstore.catalog.domain.port.out.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateCategoryUseCaseImpl implements CreateCategoryUseCase {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CreateCategoryResult execute(CreateCategoryCommand command) {
        if (categoryRepository.existsByName(command.name())) {
            throw new CategoryNameAlreadyExistsException(command.name());
        }

        var category = Category.create(command.name(), command.description());
        var saved = categoryRepository.save(category);

        return new CreateCategoryResult(
                saved.getId().value(),
                saved.getName(),
                saved.getSlug().value(),
                saved.getDescription(),
                saved.isActive(),
                saved.getCreatedAt()
        );
    }
}
