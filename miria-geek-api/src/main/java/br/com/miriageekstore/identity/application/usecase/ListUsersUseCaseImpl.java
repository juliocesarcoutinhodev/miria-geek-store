package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.model.UserRole;
import br.com.miriageekstore.identity.domain.model.UserStatus;
import br.com.miriageekstore.identity.domain.port.in.ListUsersQuery;
import br.com.miriageekstore.identity.domain.port.in.ListUsersResult;
import br.com.miriageekstore.identity.domain.port.in.ListUsersUseCase;
import br.com.miriageekstore.identity.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListUsersUseCaseImpl implements ListUsersUseCase {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public ListUsersResult execute(ListUsersQuery query) {
        UserStatus status = query.status() != null ? UserStatus.valueOf(query.status()) : null;
        UserRole role = query.role() != null ? UserRole.valueOf(query.role()) : null;

        var page = userRepository.findAll(
                query.name(), query.email(), status, role,
                query.page(), query.size(), query.sort(), query.direction()
        );

        var content = page.content().stream()
                .map(user -> new ListUsersResult.UserSummary(
                        user.getId().value(),
                        user.getName().value(),
                        user.getEmail().value(),
                        user.getRoles().iterator().next().name(),
                        user.getStatus().name(),
                        user.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new ListUsersResult(content, page.page(), page.size(), page.totalElements(), page.totalPages());
    }
}
