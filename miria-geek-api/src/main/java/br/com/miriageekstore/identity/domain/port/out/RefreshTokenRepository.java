package br.com.miriageekstore.identity.domain.port.out;

import br.com.miriageekstore.identity.domain.model.RefreshToken;

public interface RefreshTokenRepository {
    void save(RefreshToken token);
}
