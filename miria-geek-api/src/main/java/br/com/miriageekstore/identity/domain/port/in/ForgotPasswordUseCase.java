package br.com.miriageekstore.identity.domain.port.in;

public interface ForgotPasswordUseCase {
    void execute(ForgotPasswordCommand command);
}
