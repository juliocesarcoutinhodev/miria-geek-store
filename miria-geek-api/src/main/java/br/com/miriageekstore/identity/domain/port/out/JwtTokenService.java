package br.com.miriageekstore.identity.domain.port.out;

import br.com.miriageekstore.identity.domain.model.User;

public interface JwtTokenService {
    String generateAccessToken(User user);
    String generateRefreshToken();
}
