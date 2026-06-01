package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.exception.CurrentPasswordMismatchException;
import br.com.miriageekstore.identity.domain.exception.NewPasswordSameAsCurrentException;
import br.com.miriageekstore.identity.domain.exception.PasswordConfirmationException;
import br.com.miriageekstore.identity.domain.exception.UserNotFoundException;
import br.com.miriageekstore.identity.domain.model.Password;
import br.com.miriageekstore.identity.domain.model.RefreshToken;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.port.in.ChangePasswordCommand;
import br.com.miriageekstore.identity.domain.port.in.ChangePasswordResult;
import br.com.miriageekstore.identity.domain.port.in.ChangePasswordUseCase;
import br.com.miriageekstore.identity.domain.port.out.JwtTokenService;
import br.com.miriageekstore.identity.domain.port.out.PasswordHasher;
import br.com.miriageekstore.identity.domain.port.out.RefreshTokenRepository;
import br.com.miriageekstore.identity.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChangePasswordUseCaseImpl implements ChangePasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenService jwtTokenService;

    @Value("${app.jwt.refresh-token-expiry-seconds:604800}")
    private long refreshTokenExpirySeconds;

    @Override
    @Transactional
    public ChangePasswordResult execute(UserId userId, ChangePasswordCommand command) {
        if (!command.newPassword().equals(command.passwordConfirmation())) {
            throw new PasswordConfirmationException();
        }

        var user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (!passwordHasher.matches(command.currentPassword(), user.getPassword().hash())) {
            throw new CurrentPasswordMismatchException();
        }

        if (passwordHasher.matches(command.newPassword(), user.getPassword().hash())) {
            throw new NewPasswordSameAsCurrentException();
        }

        var newPassword = Password.hash(command.newPassword(), passwordHasher);
        user.changePassword(newPassword);
        userRepository.save(user);

        refreshTokenRepository.revokeAllByUserId(userId);

        var accessToken = jwtTokenService.generateAccessToken(user);
        var rawRefreshToken = jwtTokenService.generateRefreshToken();
        var newRefreshToken = RefreshToken.create(
                userId, TokenHasher.sha256(rawRefreshToken),
                command.ipAddress(), command.userAgent(),
                refreshTokenExpirySeconds
        );
        refreshTokenRepository.save(newRefreshToken);

        return new ChangePasswordResult(accessToken, rawRefreshToken);
    }
}
