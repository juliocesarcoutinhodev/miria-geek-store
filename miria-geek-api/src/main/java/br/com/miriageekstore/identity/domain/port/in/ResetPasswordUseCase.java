package br.com.miriageekstore.identity.domain.port.in;

public interface ResetPasswordUseCase {
    void execute(ResetPasswordCommand command);
}
