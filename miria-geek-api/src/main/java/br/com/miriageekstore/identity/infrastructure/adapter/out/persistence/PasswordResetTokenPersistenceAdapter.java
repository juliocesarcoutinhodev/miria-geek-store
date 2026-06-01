package br.com.miriageekstore.identity.infrastructure.adapter.out.persistence;

import br.com.miriageekstore.identity.domain.model.PasswordResetToken;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.port.out.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class PasswordResetTokenPersistenceAdapter implements PasswordResetTokenRepository {

    private final PasswordResetTokenJpaRepository jpaRepository;

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        var entity = jpaRepository.findById(token.id()).orElse(new PasswordResetTokenEntity());
        entity.setId(token.id());
        entity.setUserId(token.userId().value());
        entity.setToken(token.token());
        entity.setExpiresAt(token.expiresAt());
        entity.setUsed(token.used());
        entity.setUsedAt(token.usedAt());
        entity.setCreatedAt(token.createdAt());
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<PasswordResetToken> findByToken(UUID token) {
        return jpaRepository.findByToken(token).map(this::toDomain);
    }

    private PasswordResetToken toDomain(PasswordResetTokenEntity e) {
        return new PasswordResetToken(
                e.getId(), UserId.of(e.getUserId()), e.getToken(),
                e.getExpiresAt(), e.isUsed(), e.getUsedAt(), e.getCreatedAt());
    }
}
