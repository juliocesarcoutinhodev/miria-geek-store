package br.com.miriageekstore.identity.infrastructure.adapter.out.persistence;

import br.com.miriageekstore.identity.domain.model.RefreshToken;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.port.out.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class RefreshTokenPersistenceAdapter implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository jpaRepository;

    @Override
    public void save(RefreshToken token) {
        var entity = jpaRepository.findById(token.id()).orElse(new RefreshTokenEntity());
        entity.setId(token.id());
        entity.setUserId(token.userId().value());
        entity.setTokenHash(token.tokenHash());
        entity.setExpiresAt(token.expiresAt());
        entity.setIpAddress(token.ipAddress());
        entity.setUserAgent(token.userAgent());
        entity.setCreatedAt(token.createdAt());
        entity.setFamilyId(token.familyId());
        entity.setRevoked(token.revoked());
        entity.setRevokedAt(token.revokedAt());
        jpaRepository.save(entity);
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash).map(this::toDomain);
    }

    @Override
    public void revokeAllByFamilyId(UUID familyId) {
        jpaRepository.revokeAllByFamilyId(familyId, Instant.now());
    }

    @Override
    public void revokeAllByUserId(UserId userId) {
        jpaRepository.revokeAllByUserId(userId.value(), Instant.now());
    }

    private RefreshToken toDomain(RefreshTokenEntity e) {
        return new RefreshToken(
                e.getId(),
                UserId.of(e.getUserId()),
                e.getTokenHash(),
                e.getExpiresAt(),
                e.getIpAddress(),
                e.getUserAgent(),
                e.getCreatedAt(),
                e.getFamilyId(),
                e.isRevoked(),
                e.getRevokedAt()
        );
    }
}
