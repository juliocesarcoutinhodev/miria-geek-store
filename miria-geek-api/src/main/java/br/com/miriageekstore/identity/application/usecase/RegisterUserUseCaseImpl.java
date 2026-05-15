package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.event.UserRegistered;
import br.com.miriageekstore.identity.domain.exception.EmailAlreadyExistsException;
import br.com.miriageekstore.identity.domain.exception.PasswordConfirmationException;
import br.com.miriageekstore.identity.domain.model.Email;
import br.com.miriageekstore.identity.domain.model.FullName;
import br.com.miriageekstore.identity.domain.model.Password;
import br.com.miriageekstore.identity.domain.model.User;
import br.com.miriageekstore.identity.domain.model.VerificationToken;
import br.com.miriageekstore.identity.domain.port.in.RegisterUserCommand;
import br.com.miriageekstore.identity.domain.port.in.RegisterUserResult;
import br.com.miriageekstore.identity.domain.port.in.RegisterUserUseCase;
import br.com.miriageekstore.identity.domain.port.out.DomainEventPublisher;
import br.com.miriageekstore.identity.domain.port.out.EmailSender;
import br.com.miriageekstore.identity.domain.port.out.PasswordHasher;
import br.com.miriageekstore.identity.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final DomainEventPublisher eventPublisher;
    private final EmailSender emailSender;

    @Override
    @Transactional
    public RegisterUserResult execute(RegisterUserCommand command) {
        if (!command.rawPassword().equals(command.passwordConfirmation())) {
            throw new PasswordConfirmationException();
        }

        var email = Email.of(command.email());
        var name = FullName.of(command.fullName());

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email.value());
        }

        var password = Password.hash(command.rawPassword(), passwordHasher);
        var token = VerificationToken.generate();
        var user = User.register(name, email, password, token);
        var saved = userRepository.save(user);

        eventPublisher.publish(new UserRegistered(
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

        return new RegisterUserResult(
                saved.getId().value(),
                saved.getName().value(),
                saved.getEmail().value(),
                saved.getStatus().name(),
                saved.getCreatedAt()
        );
    }
}
