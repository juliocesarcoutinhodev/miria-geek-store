package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.port.in.LogoutAllUseCase;
import br.com.miriageekstore.identity.domain.port.out.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogoutAllUseCaseImpl implements LogoutAllUseCase {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public void execute(String rawRefreshToken) {
        if (rawRefreshToken == null) return;

        refreshTokenRepository.findByTokenHash(TokenHasher.sha256(rawRefreshToken))
                .ifPresent(t -> refreshTokenRepository.revokeAllByUserId(t.userId()));
    }
}
