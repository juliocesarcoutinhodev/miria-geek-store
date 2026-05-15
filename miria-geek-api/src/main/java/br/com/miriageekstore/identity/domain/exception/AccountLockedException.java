package br.com.miriageekstore.identity.domain.exception;

public class AccountLockedException extends RuntimeException {
    public AccountLockedException() {
        super("Account temporarily locked due to too many failed attempts. Try again in 15 minutes.");
    }
}
