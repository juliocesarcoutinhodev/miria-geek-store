package br.com.miriageekstore.identity.domain.port.out;

import br.com.miriageekstore.identity.domain.model.Email;
import br.com.miriageekstore.identity.domain.model.User;
import br.com.miriageekstore.identity.domain.model.UserId;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    boolean existsByEmail(Email email);
    User save(User user);
    Optional<User> findById(UserId id);
    Optional<User> findByEmail(Email email);
    Optional<User> findByVerificationToken(UUID token);
}
