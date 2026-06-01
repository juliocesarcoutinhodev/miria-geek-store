package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.port.in.LogoutUseCase;
import br.com.miriageekstore.identity.domain.port.out.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogoutUseCaseImpl implements LogoutUseCase {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public void execute(String rawRefreshToken) {
        if (rawRefreshToken == null) return;

        refreshTokenRepository.findByTokenHash(TokenHasher.sha256(rawRefreshToken))
                .filter(t -> !t.revoked())
                .ifPresent(t -> refreshTokenRepository.save(t.revoke()));
    }
}
