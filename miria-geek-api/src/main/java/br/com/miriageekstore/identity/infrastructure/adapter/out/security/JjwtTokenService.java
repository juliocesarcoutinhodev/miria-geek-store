package br.com.miriageekstore.identity.infrastructure.adapter.out.security;

import br.com.miriageekstore.identity.domain.model.User;
import br.com.miriageekstore.identity.domain.port.out.JwtTokenService;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
class JjwtTokenService implements JwtTokenService {

    private final SecretKey signingKey;
    private final long accessTokenExpirySeconds;
    private final String issuer;

    JjwtTokenService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-expiry-seconds:900}") long accessTokenExpirySeconds,
            @Value("${app.jwt.issuer:miria-geek-api}") String issuer) {
        this.signingKey = new SecretKeySpec(Base64.getDecoder().decode(secret), "HmacSHA256");
        this.accessTokenExpirySeconds = accessTokenExpirySeconds;
        this.issuer = issuer;
    }

    @Override
    public String generateAccessToken(User user) {
        var now = Instant.now();
        var roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .issuer(issuer)
                .subject(user.getId().value().toString())
                .claim("email", user.getEmail().value())
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTokenExpirySeconds)))
                .signWith(signingKey)
                .compact();
    }

    @Override
    public String generateRefreshToken() {
        return UUID.randomUUID().toString().replace("-", "") +
               UUID.randomUUID().toString().replace("-", "");
    }
}
