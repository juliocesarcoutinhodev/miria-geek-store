package br.com.miriageekstore.catalog.application.usecase;

import br.com.miriageekstore.catalog.domain.exception.CategoryHasProductsException;
import br.com.miriageekstore.catalog.domain.exception.CategoryNotFoundException;
import br.com.miriageekstore.catalog.domain.model.CategoryId;
import br.com.miriageekstore.catalog.domain.port.in.DeleteCategoryUseCase;
import br.com.miriageekstore.catalog.domain.port.out.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteCategoryUseCaseImpl implements DeleteCategoryUseCase {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public void execute(CategoryId id) {
        if (categoryRepository.findById(id).isEmpty()) {
            throw new CategoryNotFoundException();
        }

        if (categoryRepository.hasProducts(id)) {
            throw new CategoryHasProductsException();
        }

        categoryRepository.deleteById(id);
    }
}
