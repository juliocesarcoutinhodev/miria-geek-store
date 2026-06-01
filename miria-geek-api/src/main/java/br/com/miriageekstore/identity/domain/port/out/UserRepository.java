package br.com.miriageekstore.identity.domain.port.out;

import br.com.miriageekstore.identity.domain.model.Email;
import br.com.miriageekstore.identity.domain.model.User;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.model.UserRole;
import br.com.miriageekstore.identity.domain.model.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    boolean existsByEmail(Email email);
    boolean existsByEmailExcludingId(Email email, UserId excludedId);
    User save(User user);
    Optional<User> findById(UserId id);
    Optional<User> findByEmail(Email email);
    Optional<User> findByVerificationToken(UUID token);
    UserPage findAll(String name, String email, UserStatus status, UserRole role,
                     int page, int size, String sort, String direction);

    record UserPage(List<User> content, int page, int size, long totalElements, int totalPages) {}
}
