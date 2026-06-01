package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.exception.InvalidCredentialsException;
import br.com.miriageekstore.identity.domain.exception.PasswordConfirmationException;
import br.com.miriageekstore.identity.domain.exception.PasswordResetTokenAlreadyUsedException;
import br.com.miriageekstore.identity.domain.exception.PasswordResetTokenExpiredException;
import br.com.miriageekstore.identity.domain.exception.PasswordResetTokenNotFoundException;
import br.com.miriageekstore.identity.domain.model.Password;
import br.com.miriageekstore.identity.domain.port.in.ResetPasswordCommand;
import br.com.miriageekstore.identity.domain.port.in.ResetPasswordUseCase;
import br.com.miriageekstore.identity.domain.port.out.PasswordHasher;
import br.com.miriageekstore.identity.domain.port.out.PasswordResetTokenRepository;
import br.com.miriageekstore.identity.domain.port.out.RefreshTokenRepository;
import br.com.miriageekstore.identity.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResetPasswordUseCaseImpl implements ResetPasswordUseCase {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordHasher passwordHasher;

    @Override
    @Transactional
    public void execute(ResetPasswordCommand command) {
        if (!command.newPassword().equals(command.passwordConfirmation())) {
            throw new PasswordConfirmationException();
        }

        var resetToken = passwordResetTokenRepository.findByToken(command.token())
                .orElseThrow(PasswordResetTokenNotFoundException::new);

        if (resetToken.used()) {
            throw new PasswordResetTokenAlreadyUsedException();
        }

        if (resetToken.isExpired()) {
            throw new PasswordResetTokenExpiredException();
        }

        var user = userRepository.findById(resetToken.userId())
                .orElseThrow(InvalidCredentialsException::new);

        var newPassword = Password.hash(command.newPassword(), passwordHasher);
        user.changePassword(newPassword);

        passwordResetTokenRepository.save(resetToken.markAsUsed());
        userRepository.save(user);
        refreshTokenRepository.revokeAllByUserId(user.getId());
    }
}
