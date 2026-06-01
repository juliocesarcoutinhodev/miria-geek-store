package br.com.miriageekstore.identity.infrastructure.adapter.out.persistence;

import br.com.miriageekstore.identity.domain.model.Email;
import br.com.miriageekstore.identity.domain.model.User;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.model.UserRole;
import br.com.miriageekstore.identity.domain.model.UserStatus;
import br.com.miriageekstore.identity.domain.port.out.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserEntityMapper mapper;

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.value());
    }

    @Override
    public boolean existsByEmailExcludingId(Email email, UserId excludedId) {
        return jpaRepository.existsByEmailAndIdNot(email.value(), excludedId.value());
    }

    @Override
    public User save(User user) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(user)));
    }

    @Override
    public Optional<User> findById(UserId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByVerificationToken(UUID token) {
        return jpaRepository.findByVerificationToken(token).map(mapper::toDomain);
    }

    @Override
    public UserPage findAll(String name, String email, UserStatus status, UserRole role,
                            int page, int size, String sort, String direction) {
        var sortField = resolveSortField(sort);
        var sortDir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        var pageable = PageRequest.of(page, size, Sort.by(sortDir, sortField));

        Specification<UserEntity> spec = (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("fullName")), "%" + name.toLowerCase() + "%"));
            }
            if (email != null && !email.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (role != null) {
                var subquery = query.subquery(UUID.class);
                var rolesRoot = subquery.from(UserEntity.class);
                subquery.select(rolesRoot.get("id"))
                        .where(
                                cb.equal(rolesRoot.get("id"), root.get("id")),
                                cb.isMember(role, rolesRoot.get("roles"))
                        );
                predicates.add(cb.exists(subquery));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        var resultPage = jpaRepository.findAll(spec, pageable);
        var content = resultPage.getContent().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());

        return new UserPage(content, page, size, resultPage.getTotalElements(), resultPage.getTotalPages());
    }

    private String resolveSortField(String sort) {
        if (sort == null) return "createdAt";
        return switch (sort) {
            case "nome", "fullName" -> "fullName";
            case "email" -> "email";
            case "status" -> "status";
            case "dataCadastro", "createdAt" -> "createdAt";
            default -> "createdAt";
        };
    }
}
