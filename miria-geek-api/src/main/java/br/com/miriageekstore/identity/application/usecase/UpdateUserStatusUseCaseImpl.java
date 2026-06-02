package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.exception.CannotDeactivateOwnAccountException;
import br.com.miriageekstore.identity.domain.exception.UserNotFoundException;
import br.com.miriageekstore.identity.domain.model.AuditAction;
import br.com.miriageekstore.identity.domain.model.AuditLog;
import br.com.miriageekstore.identity.domain.model.UserStatus;
import br.com.miriageekstore.identity.domain.port.in.UpdateUserStatusCommand;
import br.com.miriageekstore.identity.domain.port.in.UpdateUserStatusResult;
import br.com.miriageekstore.identity.domain.port.in.UpdateUserStatusUseCase;
import br.com.miriageekstore.identity.domain.port.out.AuditLogRepository;
import br.com.miriageekstore.identity.domain.port.out.RefreshTokenRepository;
import br.com.miriageekstore.identity.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateUserStatusUseCaseImpl implements UpdateUserStatusUseCase {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public UpdateUserStatusResult execute(UpdateUserStatusCommand command) {
        if (command.newStatus() != UserStatus.ACTIVE && command.newStatus() != UserStatus.INACTIVE) {
            throw new IllegalArgumentException("Status inválido: deve ser ACTIVE ou INACTIVE");
        }

        if (command.targetUserId().equals(command.requesterId())) {
            throw new CannotDeactivateOwnAccountException();
        }

        var user = userRepository.findById(command.targetUserId())
                .orElseThrow(UserNotFoundException::new);

        user.updateStatus(command.newStatus());
        userRepository.save(user);

        if (command.newStatus() == UserStatus.INACTIVE) {
            refreshTokenRepository.revokeAllByUserId(command.targetUserId());
        }

        auditLogRepository.save(AuditLog.create(
                user.getId().value(), command.requesterId().value(), AuditAction.USER_STATUS_CHANGED));

        return new UpdateUserStatusResult(
                user.getId().value(),
                user.getName().value(),
                user.getEmail().value(),
                user.getStatus().name()
        );
    }
}
