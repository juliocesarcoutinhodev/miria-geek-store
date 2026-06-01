package br.com.miriageekstore.identity.infrastructure.adapter.out.persistence;

import br.com.miriageekstore.identity.domain.model.Email;
import br.com.miriageekstore.identity.domain.model.FullName;
import br.com.miriageekstore.identity.domain.model.Password;
import br.com.miriageekstore.identity.domain.model.User;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.model.VerificationToken;
import org.springframework.stereotype.Component;

@Component
class UserEntityMapper {

    UserEntity toEntity(User user) {
        var entity = new UserEntity();
        entity.setId(user.getId().value());
        entity.setFullName(user.getName().value());
        entity.setEmail(user.getEmail().value());
        entity.setPassword(user.getPassword().hash());
        entity.setStatus(user.getStatus());
        entity.setRoles(new java.util.HashSet<>(user.getRoles()));
        var token = user.getVerificationToken();
        if (token != null) {
            entity.setVerificationToken(token.token());
            entity.setVerificationTokenExpiresAt(token.expiresAt());
        }
        entity.setCreatedAt(user.getCreatedAt());
        entity.setCreatedByAdminId(user.getCreatedByAdminId());
        entity.setLastLoginAt(user.getLastLoginAt());
        return entity;
    }

    User toDomain(UserEntity entity) {
        VerificationToken token = entity.getVerificationToken() != null
                ? new VerificationToken(entity.getVerificationToken(), entity.getVerificationTokenExpiresAt())
                : null;
        return User.reconstitute(
                UserId.of(entity.getId()),
                FullName.of(entity.getFullName()),
                Email.of(entity.getEmail()),
                Password.fromHash(entity.getPassword()),
                entity.getStatus(),
                entity.getRoles(),
                token,
                entity.getCreatedAt(),
                entity.getCreatedByAdminId(),
                entity.getLastLoginAt()
        );
    }
}
