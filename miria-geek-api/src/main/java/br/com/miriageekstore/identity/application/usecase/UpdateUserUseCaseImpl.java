package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.exception.CannotChangeOwnRoleException;
import br.com.miriageekstore.identity.domain.exception.EmailAlreadyExistsException;
import br.com.miriageekstore.identity.domain.exception.UserNotFoundException;
import br.com.miriageekstore.identity.domain.model.AuditAction;
import br.com.miriageekstore.identity.domain.model.AuditLog;
import br.com.miriageekstore.identity.domain.model.Email;
import br.com.miriageekstore.identity.domain.model.FullName;
import br.com.miriageekstore.identity.domain.model.UserRole;
import br.com.miriageekstore.identity.domain.port.in.UpdateUserCommand;
import br.com.miriageekstore.identity.domain.port.in.UpdateUserResult;
import br.com.miriageekstore.identity.domain.port.in.UpdateUserUseCase;
import br.com.miriageekstore.identity.domain.port.out.AuditLogRepository;
import br.com.miriageekstore.identity.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UpdateUserUseCaseImpl implements UpdateUserUseCase {

    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public UpdateUserResult execute(UpdateUserCommand command) {
        var user = userRepository.findById(command.targetUserId())
                .orElseThrow(UserNotFoundException::new);

        var newEmail = Email.of(command.email());
        if (!user.getEmail().equals(newEmail)
                && userRepository.existsByEmailExcludingId(newEmail, command.targetUserId())) {
            throw new EmailAlreadyExistsException(newEmail.value());
        }

        var newRole = UserRole.valueOf(command.role());
        if (command.adminId().equals(command.targetUserId())
                && !user.getRoles().equals(Set.of(newRole))) {
            throw new CannotChangeOwnRoleException();
        }

        user.changeName(FullName.of(command.fullName()));
        user.changeEmail(newEmail);
        user.changeRoles(Set.of(newRole));

        var saved = userRepository.save(user);

        auditLogRepository.save(AuditLog.create(
                saved.getId().value(), command.adminId().value(), AuditAction.USER_UPDATED));

        return new UpdateUserResult(
                saved.getId().value(),
                saved.getName().value(),
                saved.getEmail().value(),
                saved.getRoles().iterator().next().name(),
                saved.getStatus().name(),
                saved.getCreatedAt()
        );
    }
}
