package br.com.miriageekstore.identity.infrastructure.adapter.out.persistence;

import br.com.miriageekstore.identity.domain.model.RefreshToken;
import br.com.miriageekstore.identity.domain.port.out.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class RefreshTokenPersistenceAdapter implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository jpaRepository;

    @Override
    public void save(RefreshToken token) {
        var entity = new RefreshTokenEntity();
        entity.setId(token.id());
        entity.setUserId(token.userId().value());
        entity.setTokenHash(token.tokenHash());
        entity.setExpiresAt(token.expiresAt());
        entity.setIpAddress(token.ipAddress());
        entity.setUserAgent(token.userAgent());
        entity.setCreatedAt(token.createdAt());
        jpaRepository.save(entity);
    }
}
