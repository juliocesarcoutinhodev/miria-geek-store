package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.event.AdminUserCreated;
import br.com.miriageekstore.identity.domain.exception.EmailAlreadyExistsException;
import br.com.miriageekstore.identity.domain.model.Email;
import br.com.miriageekstore.identity.domain.model.FullName;
import br.com.miriageekstore.identity.domain.model.Password;
import br.com.miriageekstore.identity.domain.model.User;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.port.in.CreateAdminUserCommand;
import br.com.miriageekstore.identity.domain.port.in.CreateAdminUserResult;
import br.com.miriageekstore.identity.domain.port.in.CreateAdminUserUseCase;
import br.com.miriageekstore.identity.domain.port.out.DomainEventPublisher;
import br.com.miriageekstore.identity.domain.port.out.EmailSender;
import br.com.miriageekstore.identity.domain.port.out.PasswordHasher;
import br.com.miriageekstore.identity.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateAdminUserUseCaseImpl implements CreateAdminUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final DomainEventPublisher eventPublisher;
    private final EmailSender emailSender;

    @Override
    @Transactional
    public CreateAdminUserResult execute(UserId createdByAdminId, CreateAdminUserCommand command) {
        var email = Email.of(command.email());
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email.value());
        }

        var tempPassword = generateTempPassword();
        var password = Password.hash(tempPassword, passwordHasher);
        var name = FullName.of(command.fullName());
        var user = User.createAdmin(name, email, password, createdByAdminId);
        var saved = userRepository.save(user);

        eventPublisher.publish(new AdminUserCreated(
                saved.getId().value(), saved.getEmail().value(),
                saved.getName().value(), createdByAdminId.value(), Instant.now()));

        emailSender.sendAdminWelcomeEmail(
                saved.getEmail().value(), saved.getName().value(), tempPassword);

        return new CreateAdminUserResult(
                saved.getId().value(), saved.getName().value(), saved.getEmail().value(),
                saved.getStatus().name(), "ROLE_ADMIN",
                saved.getCreatedAt(), createdByAdminId.value()
        );
    }

    private static String generateTempPassword() {
        // UUID hex (0-9, a-f) always satisfies the password policy (letters + digits)
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}
