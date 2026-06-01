package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.exception.UserNotFoundException;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.port.in.GetProfileUseCase;
import br.com.miriageekstore.identity.domain.port.in.ProfileResult;
import br.com.miriageekstore.identity.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetProfileUseCaseImpl implements GetProfileUseCase {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public ProfileResult execute(UserId userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        var roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return new ProfileResult(
                user.getId().value(),
                user.getName().value(),
                user.getEmail().value(),
                user.getStatus().name(),
                roles,
                user.getCreatedAt()
        );
    }
}
