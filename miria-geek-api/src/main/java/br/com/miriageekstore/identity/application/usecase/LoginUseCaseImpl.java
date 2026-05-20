package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.event.UserLoggedIn;
import br.com.miriageekstore.identity.domain.exception.AccountLockedException;
import br.com.miriageekstore.identity.domain.exception.AccountNotActiveException;
import br.com.miriageekstore.identity.domain.exception.InvalidCredentialsException;
import br.com.miriageekstore.identity.domain.model.Email;
import br.com.miriageekstore.identity.domain.model.RefreshToken;
import br.com.miriageekstore.identity.domain.model.UserStatus;
import br.com.miriageekstore.identity.domain.port.in.LoginCommand;
import br.com.miriageekstore.identity.domain.port.in.LoginResult;
import br.com.miriageekstore.identity.domain.port.in.LoginUseCase;
import br.com.miriageekstore.identity.domain.port.out.DomainEventPublisher;
import br.com.miriageekstore.identity.domain.port.out.EmailSender;
import br.com.miriageekstore.identity.domain.port.out.JwtTokenService;
import br.com.miriageekstore.identity.domain.port.out.LoginAttemptTracker;
import br.com.miriageekstore.identity.domain.port.out.PasswordHasher;
import br.com.miriageekstore.identity.domain.port.out.RefreshTokenRepository;
import br.com.miriageekstore.identity.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoginUseCaseImpl implements LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final LoginAttemptTracker attemptTracker;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final DomainEventPublisher eventPublisher;
    private final EmailSender emailSender;

    @Value("${app.jwt.refresh-token-expiry-seconds:2592000}")
    private long refreshTokenExpirySeconds;

    @Override
    @Transactional
    public LoginResult execute(LoginCommand command) {
        if (attemptTracker.isLocked(command.email())) {
            throw new AccountLockedException();
        }

        var userOpt = userRepository.findByEmail(Email.of(command.email()));
        if (userOpt.isEmpty()) {
            attemptTracker.recordFailure(command.email());
            throw new InvalidCredentialsException();
        }

        var user = userOpt.get();

        if (!passwordHasher.matches(command.password(), user.getPassword().hash())) {
            attemptTracker.recordFailure(command.email());
            throw new InvalidCredentialsException();
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AccountNotActiveException(user.getStatus());
        }

        attemptTracker.resetFailures(command.email());

        var accessToken = jwtTokenService.generateAccessToken(user);
        var rawRefreshToken = jwtTokenService.generateRefreshToken();
        var tokenHash = TokenHasher.sha256(rawRefreshToken);

        var refreshToken = RefreshToken.create(
                user.getId(), tokenHash,
                command.ipAddress(), command.userAgent(),
                refreshTokenExpirySeconds
        );
        refreshTokenRepository.save(refreshToken);

        var now = Instant.now();
        eventPublisher.publish(new UserLoggedIn(
                user.getId().value(), user.getEmail().value(), user.getName().value(), now));
        emailSender.sendLoginNotificationEmail(
                user.getEmail().value(), user.getName().value(),
                command.ipAddress(), command.userAgent(), now);

        var roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return new LoginResult(
                accessToken, rawRefreshToken,
                user.getId().value(), user.getName().value(),
                user.getEmail().value(), roles,
                user.getStatus().name()
        );
    }

}
