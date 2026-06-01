package br.com.miriageekstore.identity.domain.port.out;

import br.com.miriageekstore.identity.domain.model.PasswordResetToken;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository {
    PasswordResetToken save(PasswordResetToken token);
    Optional<PasswordResetToken> findByToken(UUID token);
}
