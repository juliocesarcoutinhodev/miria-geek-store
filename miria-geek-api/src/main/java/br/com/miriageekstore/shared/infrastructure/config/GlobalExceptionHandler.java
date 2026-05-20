package br.com.miriageekstore.shared.infrastructure.config;

import br.com.miriageekstore.identity.domain.exception.AccountLockedException;
import br.com.miriageekstore.identity.domain.exception.AccountNotActiveException;
import br.com.miriageekstore.identity.domain.exception.EmailAlreadyExistsException;
import br.com.miriageekstore.identity.domain.exception.InvalidCredentialsException;
import br.com.miriageekstore.identity.domain.exception.InvalidEmailException;
import br.com.miriageekstore.identity.domain.exception.InvalidPasswordPolicyException;
import br.com.miriageekstore.identity.domain.exception.InvalidRefreshTokenException;
import br.com.miriageekstore.identity.domain.exception.PasswordConfirmationException;
import br.com.miriageekstore.identity.domain.exception.UserAlreadyVerifiedException;
import br.com.miriageekstore.identity.domain.exception.VerificationTokenExpiredException;
import br.com.miriageekstore.identity.domain.exception.VerificationTokenNotFoundException;
import br.com.miriageekstore.identity.infrastructure.config.CookieFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final CookieFactory cookieFactory;

    @ExceptionHandler(EmailAlreadyExistsException.class)
    ResponseEntity<ErrorResponse> handle(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("EMAIL_CONFLICT", ex.getMessage()));
    }

    @ExceptionHandler(UserAlreadyVerifiedException.class)
    ResponseEntity<ErrorResponse> handle(UserAlreadyVerifiedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("ALREADY_VERIFIED", ex.getMessage()));
    }

    @ExceptionHandler(VerificationTokenNotFoundException.class)
    ResponseEntity<ErrorResponse> handle(VerificationTokenNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("TOKEN_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(VerificationTokenExpiredException.class)
    ResponseEntity<ErrorResponse> handle(VerificationTokenExpiredException ex) {
        return ResponseEntity.status(HttpStatus.GONE)
                .body(new ErrorResponse("TOKEN_EXPIRED", ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    ResponseEntity<ErrorResponse> handle(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("INVALID_CREDENTIALS", ex.getMessage()));
    }

    @ExceptionHandler(AccountNotActiveException.class)
    ResponseEntity<ErrorResponse> handle(AccountNotActiveException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("ACCOUNT_NOT_ACTIVE", ex.getMessage()));
    }

    @ExceptionHandler(AccountLockedException.class)
    ResponseEntity<ErrorResponse> handle(AccountLockedException ex) {
        return ResponseEntity.status(423)
                .body(new ErrorResponse("ACCOUNT_LOCKED", ex.getMessage()));
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    ResponseEntity<ErrorResponse> handle(InvalidRefreshTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.SET_COOKIE, cookieFactory.clearAccessToken().toString())
                .header(HttpHeaders.SET_COOKIE, cookieFactory.clearRefreshToken().toString())
                .body(new ErrorResponse("INVALID_TOKEN", ex.getMessage()));
    }

    @ExceptionHandler({PasswordConfirmationException.class, InvalidPasswordPolicyException.class,
            InvalidEmailException.class, IllegalArgumentException.class})
    ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("BAD_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        var message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("VALIDATION_ERROR", message));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"));
    }
}
