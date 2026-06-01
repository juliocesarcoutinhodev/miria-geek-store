package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.exception.UserNotFoundException;
import br.com.miriageekstore.identity.domain.model.FullName;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.port.in.ProfileResult;
import br.com.miriageekstore.identity.domain.port.in.UpdateProfileCommand;
import br.com.miriageekstore.identity.domain.port.in.UpdateProfileUseCase;
import br.com.miriageekstore.identity.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UpdateProfileUseCaseImpl implements UpdateProfileUseCase {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public ProfileResult execute(UserId userId, UpdateProfileCommand command) {
        var user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        user.changeName(FullName.of(command.fullName()));
        var saved = userRepository.save(user);

        var roles = saved.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return new ProfileResult(
                saved.getId().value(),
                saved.getName().value(),
                saved.getEmail().value(),
                saved.getStatus().name(),
                roles,
                saved.getCreatedAt()
        );
    }
}
