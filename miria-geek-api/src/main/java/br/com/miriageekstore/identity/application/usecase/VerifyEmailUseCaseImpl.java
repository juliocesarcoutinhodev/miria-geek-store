package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.event.UserEmailVerified;
import br.com.miriageekstore.identity.domain.exception.VerificationTokenNotFoundException;
import br.com.miriageekstore.identity.domain.port.in.VerifyEmailUseCase;
import br.com.miriageekstore.identity.domain.port.out.DomainEventPublisher;
import br.com.miriageekstore.identity.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerifyEmailUseCaseImpl implements VerifyEmailUseCase {

    private final UserRepository userRepository;
    private final DomainEventPublisher eventPublisher;

    @Override
    @Transactional
    public void execute(UUID token) {
        var user = userRepository.findByVerificationToken(token)
                .orElseThrow(VerificationTokenNotFoundException::new);

        user.verifyEmail();

        userRepository.save(user);

        eventPublisher.publish(new UserEmailVerified(
                user.getId().value(),
                user.getEmail().value(),
                Instant.now()
        ));
    }
}
