package br.com.miriageekstore.identity.domain.port.out;

import br.com.miriageekstore.identity.domain.model.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {
    void save(RefreshToken token);
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    void revokeAllByFamilyId(UUID familyId);
}
