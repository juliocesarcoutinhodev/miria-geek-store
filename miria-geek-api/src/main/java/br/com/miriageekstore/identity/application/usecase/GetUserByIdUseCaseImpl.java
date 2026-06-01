package br.com.miriageekstore.identity.application.usecase;

import br.com.miriageekstore.identity.domain.exception.UserNotFoundException;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.port.in.GetUserByIdResult;
import br.com.miriageekstore.identity.domain.port.in.GetUserByIdUseCase;
import br.com.miriageekstore.identity.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetUserByIdUseCaseImpl implements GetUserByIdUseCase {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public GetUserByIdResult execute(UserId id) {
        var user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        return new GetUserByIdResult(
                user.getId().value(),
                user.getName().value(),
                user.getEmail().value(),
                user.getRoles().iterator().next().name(),
                user.getStatus().name(),
                user.getCreatedAt(),
                0,
                user.getLastLoginAt()
        );
    }
}
