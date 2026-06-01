package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.event.PasswordResetRequested;
import br.com.miriageekstore.identity.domain.model.Email;
import br.com.miriageekstore.identity.domain.model.PasswordResetToken;
import br.com.miriageekstore.identity.domain.port.in.ForgotPasswordCommand;
import br.com.miriageekstore.identity.domain.port.in.ForgotPasswordUseCase;
import br.com.miriageekstore.identity.domain.port.out.DomainEventPublisher;
import br.com.miriageekstore.identity.domain.port.out.EmailSender;
import br.com.miriageekstore.identity.domain.port.out.PasswordResetTokenRepository;
import br.com.miriageekstore.identity.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ForgotPasswordUseCaseImpl implements ForgotPasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final DomainEventPublisher eventPublisher;
    private final EmailSender emailSender;

    @Value("${app.password-reset.token-expiry-seconds:3600}")
    private long tokenExpirySeconds;

    @Override
    @Transactional
    public void execute(ForgotPasswordCommand command) {
        var userOpt = userRepository.findByEmail(Email.of(command.email()));
        if (userOpt.isEmpty()) return; // silently ignore — never confirm email existence

        var user = userOpt.get();
        var resetToken = PasswordResetToken.create(user.getId(), tokenExpirySeconds);
        passwordResetTokenRepository.save(resetToken);

        eventPublisher.publish(new PasswordResetRequested(
                user.getId().value(), user.getEmail().value(),
                user.getName().value(), resetToken.token(), Instant.now()));

        emailSender.sendPasswordResetEmail(
                user.getEmail().value(), user.getName().value(), resetToken.token());
    }
}
