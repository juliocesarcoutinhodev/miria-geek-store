package br.com.miriageekstore.identity.domain.port.in;

import java.util.UUID;

public interface VerifyEmailUseCase {
    void execute(UUID token);
}
