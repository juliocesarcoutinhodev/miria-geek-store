package br.com.miriageekstore.identity.domain.exception;

public class AccountLockedException extends RuntimeException {
    public AccountLockedException() {
        super("Conta temporariamente bloqueada por excesso de tentativas. Tente novamente em 15 minutos.");
    }
}
