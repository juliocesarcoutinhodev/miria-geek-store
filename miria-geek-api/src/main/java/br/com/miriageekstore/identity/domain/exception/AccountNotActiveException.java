package br.com.miriageekstore.identity.domain.exception;

import br.com.miriageekstore.identity.domain.model.UserStatus;

public class AccountNotActiveException extends RuntimeException {

    public AccountNotActiveException(UserStatus status) {
        super(messageFor(status));
    }

    private static String messageFor(UserStatus status) {
        return switch (status) {
            case PENDING_VERIFICATION -> "Confirme seu e-mail para continuar";
            case INACTIVE -> "Conta desativada, entre em contato com o suporte";
            default -> "Conta não autorizada para acesso";
        };
    }
}
