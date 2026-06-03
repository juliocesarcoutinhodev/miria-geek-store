package br.com.miriageekstore.catalog.infrastructure.adapter.out.persistence;

import br.com.miriageekstore.catalog.domain.model.Category;
import br.com.miriageekstore.catalog.domain.model.CategoryId;
import br.com.miriageekstore.catalog.domain.port.out.CategoryRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class CategoryPersistenceAdapter implements CategoryRepository {

    private final CategoryJpaRepository jpaRepository;
    private final CategoryEntityMapper mapper;

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    @Override
    public boolean existsByNameExcludingId(String name, CategoryId excludedId) {
        return jpaRepository.existsByNameAndIdNot(name, excludedId.value());
    }

    @Override
    public Optional<Category> findById(CategoryId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Category save(Category category) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(category)));
    }

    @Override
    public void deleteById(CategoryId id) {
        jpaRepository.deleteById(id.value());
    }

    @Override
    public CategoryPage findAll(String name, Boolean active,
                                int page, int size, String sort, String direction) {
        var sortField = resolveSortField(sort);
        var sortDir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        var pageable = PageRequest.of(page, size, Sort.by(sortDir, sortField));

        Specification<CategoryEntity> spec = (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (active != null) {
                predicates.add(cb.equal(root.get("active"), active));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        var resultPage = jpaRepository.findAll(spec, pageable);
        var content = resultPage.getContent().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());

        return new CategoryPage(content, page, size, resultPage.getTotalElements(), resultPage.getTotalPages());
    }

    @Override
    public List<Category> findAllActive() {
        return jpaRepository.findAllByActiveTrueOrderByNameAsc().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean hasProducts(CategoryId id) {
        return false;
    }

    private String resolveSortField(String sort) {
        if (sort == null) return "name";
        return switch (sort) {
            case "name", "nome" -> "name";
            case "active", "ativo" -> "active";
            case "createdAt", "dataCriacao" -> "createdAt";
            default -> "name";
        };
    }
}
