package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.event.UserVerificationResent;
import br.com.miriageekstore.identity.domain.model.Email;
import br.com.miriageekstore.identity.domain.model.UserStatus;
import br.com.miriageekstore.identity.domain.port.in.ResendVerificationUseCase;
import br.com.miriageekstore.identity.domain.port.out.DomainEventPublisher;
import br.com.miriageekstore.identity.domain.port.out.EmailSender;
import br.com.miriageekstore.identity.domain.port.out.ResendRateLimiter;
import br.com.miriageekstore.identity.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ResendVerificationUseCaseImpl implements ResendVerificationUseCase {

    private final UserRepository userRepository;
    private final DomainEventPublisher eventPublisher;
    private final ResendRateLimiter rateLimiter;
    private final EmailSender emailSender;

    @Override
    @Transactional
    public void execute(String email) {
        var optionalUser = userRepository.findByEmail(Email.of(email));
        if (optionalUser.isEmpty()) return;

        var user = optionalUser.get();
        if (user.getStatus() != UserStatus.PENDING_VERIFICATION) return;
        if (!rateLimiter.isAllowed(email)) return;

        rateLimiter.record(email);
        user.regenerateVerificationToken();
        var saved = userRepository.save(user);

        eventPublisher.publish(new UserVerificationResent(
                saved.getId().value(),
                saved.getEmail().value(),
                saved.getName().value(),
                saved.getVerificationToken().token(),
                Instant.now()
        ));
        emailSender.sendVerificationEmail(
                saved.getEmail().value(),
                saved.getName().value(),
                saved.getVerificationToken().token());
    }
}
