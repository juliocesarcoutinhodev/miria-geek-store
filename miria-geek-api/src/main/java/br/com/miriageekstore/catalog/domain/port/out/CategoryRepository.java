package br.com.miriageekstore.catalog.domain.port.out;

import br.com.miriageekstore.catalog.domain.model.Category;
import br.com.miriageekstore.catalog.domain.model.CategoryId;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    boolean existsByName(String name);

    boolean existsByNameExcludingId(String name, CategoryId excludedId);

    Optional<Category> findById(CategoryId id);

    Category save(Category category);

    void deleteById(CategoryId id);

    CategoryPage findAll(String name, Boolean active, int page, int size, String sort, String direction);

    List<Category> findAllActive();

    boolean hasProducts(CategoryId id);

    record CategoryPage(List<Category> content, int page, int size, long totalElements, int totalPages) {}
}
