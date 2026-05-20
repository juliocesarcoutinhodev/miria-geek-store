package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.exception.InvalidRefreshTokenException;
import br.com.miriageekstore.identity.domain.model.RefreshToken;
import br.com.miriageekstore.identity.domain.port.in.LoginResult;
import br.com.miriageekstore.identity.domain.port.in.RefreshTokenUseCase;
import br.com.miriageekstore.identity.domain.port.out.JwtTokenService;
import br.com.miriageekstore.identity.domain.port.out.RefreshTokenRepository;
import br.com.miriageekstore.identity.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RefreshTokenUseCaseImpl implements RefreshTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;

    @Value("${app.jwt.refresh-token-expiry-seconds:604800}")
    private long refreshTokenExpirySeconds;

    @Override
    @Transactional
    public LoginResult execute(String rawToken, String ipAddress, String userAgent) {
        if (rawToken == null) {
            throw new InvalidRefreshTokenException();
        }

        var token = refreshTokenRepository.findByTokenHash(TokenHasher.sha256(rawToken))
                .orElseThrow(InvalidRefreshTokenException::new);

        if (token.revoked()) {
            refreshTokenRepository.revokeAllByFamilyId(token.familyId());
            throw new InvalidRefreshTokenException();
        }

        if (token.isExpired()) {
            refreshTokenRepository.save(token.revoke());
            throw new InvalidRefreshTokenException();
        }

        refreshTokenRepository.save(token.revoke());

        var user = userRepository.findById(token.userId())
                .orElseThrow(InvalidRefreshTokenException::new);

        var accessToken = jwtTokenService.generateAccessToken(user);
        var rawRefreshToken = jwtTokenService.generateRefreshToken();
        var rotatedToken = RefreshToken.createRotated(
                user.getId(), TokenHasher.sha256(rawRefreshToken),
                ipAddress, userAgent,
                refreshTokenExpirySeconds, token.familyId()
        );
        refreshTokenRepository.save(rotatedToken);

        var roles = user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());
        return new LoginResult(accessToken, rawRefreshToken,
                user.getId().value(), user.getName().value(),
                user.getEmail().value(), roles, user.getStatus().name());
    }
}
