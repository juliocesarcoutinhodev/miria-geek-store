package br.com.miriageekstore.shared.infrastructure.config;

import br.com.miriageekstore.cart.domain.exception.CartInsufficientStockException;
import br.com.miriageekstore.cart.domain.exception.CartItemNotFoundException;
import br.com.miriageekstore.cart.domain.exception.CartItemNotOwnedException;
import br.com.miriageekstore.cart.domain.exception.CartVariantNotFoundException;
import br.com.miriageekstore.cart.domain.exception.ProductInactiveException;
import br.com.miriageekstore.cart.domain.exception.VariantInactiveException;
import br.com.miriageekstore.cart.domain.exception.VariantMissingDimensionsException;
import br.com.miriageekstore.catalog.domain.exception.CannotRemovePrincipalImageException;
import br.com.miriageekstore.catalog.domain.exception.InsufficientStockException;
import br.com.miriageekstore.catalog.domain.exception.LastActiveVariantException;
import br.com.miriageekstore.catalog.domain.exception.ProductCannotBeActivatedException;
import br.com.miriageekstore.catalog.domain.exception.VariantNotFoundException;
import br.com.miriageekstore.catalog.domain.exception.CategoryHasProductsException;
import br.com.miriageekstore.catalog.domain.exception.CategoryNameAlreadyExistsException;
import br.com.miriageekstore.catalog.domain.exception.CategoryNotFoundException;
import br.com.miriageekstore.catalog.domain.exception.ImageLimitExceededException;
import br.com.miriageekstore.catalog.domain.exception.InvalidImageTypeException;
import br.com.miriageekstore.catalog.domain.exception.ProductImageNotFoundException;
import br.com.miriageekstore.catalog.domain.exception.ProductNotFoundException;
import br.com.miriageekstore.order.domain.exception.InvalidOrderStatusTransitionException;
import br.com.miriageekstore.order.domain.exception.OrderNotFoundException;
import br.com.miriageekstore.order.domain.exception.OrderNotShippedException;
import br.com.miriageekstore.catalog.domain.exception.ProductSkuAlreadyExistsException;
import br.com.miriageekstore.catalog.domain.exception.StorageException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import br.com.miriageekstore.identity.domain.exception.AccountLockedException;
import br.com.miriageekstore.identity.domain.exception.CannotChangeOwnRoleException;
import br.com.miriageekstore.identity.domain.exception.AccountNotActiveException;
import br.com.miriageekstore.identity.domain.exception.AddressLinkedToActiveOrderException;
import br.com.miriageekstore.identity.domain.exception.CannotDeactivateOwnAccountException;
import br.com.miriageekstore.identity.domain.exception.AddressLimitExceededException;
import br.com.miriageekstore.identity.domain.exception.AddressNotFoundException;
import br.com.miriageekstore.identity.domain.exception.CurrentPasswordMismatchException;
import br.com.miriageekstore.identity.domain.exception.EmailAlreadyExistsException;
import br.com.miriageekstore.identity.domain.exception.NewPasswordSameAsCurrentException;
import br.com.miriageekstore.identity.domain.exception.PasswordResetTokenAlreadyUsedException;
import br.com.miriageekstore.identity.domain.exception.PasswordResetTokenExpiredException;
import br.com.miriageekstore.identity.domain.exception.PasswordResetTokenNotFoundException;
import br.com.miriageekstore.identity.domain.exception.UserNotFoundException;
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

    @ExceptionHandler(CannotDeactivateOwnAccountException.class)
    ResponseEntity<ErrorResponse> handle(CannotDeactivateOwnAccountException ex) {
        return ResponseEntity.status(422)
                .body(new ErrorResponse("CANNOT_DEACTIVATE_OWN_ACCOUNT", ex.getMessage()));
    }

    @ExceptionHandler(CannotChangeOwnRoleException.class)
    ResponseEntity<ErrorResponse> handle(CannotChangeOwnRoleException ex) {
        return ResponseEntity.status(422)
                .body(new ErrorResponse("CANNOT_CHANGE_OWN_ROLE", ex.getMessage()));
    }

    @ExceptionHandler(AddressLimitExceededException.class)
    ResponseEntity<ErrorResponse> handle(AddressLimitExceededException ex) {
        return ResponseEntity.status(422)
                .body(new ErrorResponse("ADDRESS_LIMIT_EXCEEDED", ex.getMessage()));
    }

    @ExceptionHandler(AddressNotFoundException.class)
    ResponseEntity<ErrorResponse> handle(AddressNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("ADDRESS_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(AddressLinkedToActiveOrderException.class)
    ResponseEntity<ErrorResponse> handle(AddressLinkedToActiveOrderException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("ADDRESS_LINKED_TO_ACTIVE_ORDER", ex.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<ErrorResponse> handle(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("USER_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(PasswordResetTokenNotFoundException.class)
    ResponseEntity<ErrorResponse> handle(PasswordResetTokenNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("RESET_TOKEN_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(PasswordResetTokenExpiredException.class)
    ResponseEntity<ErrorResponse> handle(PasswordResetTokenExpiredException ex) {
        return ResponseEntity.status(HttpStatus.GONE)
                .body(new ErrorResponse("RESET_TOKEN_EXPIRED", ex.getMessage()));
    }

    @ExceptionHandler(PasswordResetTokenAlreadyUsedException.class)
    ResponseEntity<ErrorResponse> handle(PasswordResetTokenAlreadyUsedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("RESET_TOKEN_ALREADY_USED", ex.getMessage()));
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    ResponseEntity<ErrorResponse> handle(InvalidRefreshTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.SET_COOKIE, cookieFactory.clearAccessToken().toString())
                .header(HttpHeaders.SET_COOKIE, cookieFactory.clearRefreshToken().toString())
                .body(new ErrorResponse("INVALID_TOKEN", ex.getMessage()));
    }

    @ExceptionHandler(CurrentPasswordMismatchException.class)
    ResponseEntity<ErrorResponse> handle(CurrentPasswordMismatchException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("CURRENT_PASSWORD_MISMATCH", ex.getMessage()));
    }

    @ExceptionHandler(NewPasswordSameAsCurrentException.class)
    ResponseEntity<ErrorResponse> handle(NewPasswordSameAsCurrentException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("NEW_PASSWORD_SAME_AS_CURRENT", ex.getMessage()));
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

    @ExceptionHandler(CategoryNotFoundException.class)
    ResponseEntity<ErrorResponse> handle(CategoryNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("CATEGORY_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(CategoryNameAlreadyExistsException.class)
    ResponseEntity<ErrorResponse> handle(CategoryNameAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("CATEGORY_NAME_CONFLICT", ex.getMessage()));
    }

    @ExceptionHandler(CategoryHasProductsException.class)
    ResponseEntity<ErrorResponse> handle(CategoryHasProductsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("CATEGORY_HAS_PRODUCTS", ex.getMessage()));
    }

    @ExceptionHandler(ProductSkuAlreadyExistsException.class)
    ResponseEntity<ErrorResponse> handle(ProductSkuAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("SKU_CONFLICT", ex.getMessage()));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    ResponseEntity<ErrorResponse> handle(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("PRODUCT_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    ResponseEntity<ErrorResponse> handle(OrderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("ORDER_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(InvalidOrderStatusTransitionException.class)
    ResponseEntity<ErrorResponse> handle(InvalidOrderStatusTransitionException ex) {
        return ResponseEntity.status(422)
                .body(new ErrorResponse("INVALID_ORDER_STATUS_TRANSITION", ex.getMessage()));
    }

    @ExceptionHandler(OrderNotShippedException.class)
    ResponseEntity<ErrorResponse> handle(OrderNotShippedException ex) {
        return ResponseEntity.status(422)
                .body(new ErrorResponse("ORDER_NOT_SHIPPED", ex.getMessage()));
    }

    @ExceptionHandler(ProductImageNotFoundException.class)
    ResponseEntity<ErrorResponse> handle(ProductImageNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("IMAGE_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(InvalidImageTypeException.class)
    ResponseEntity<ErrorResponse> handle(InvalidImageTypeException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("INVALID_IMAGE_TYPE", ex.getMessage()));
    }

    @ExceptionHandler(ImageLimitExceededException.class)
    ResponseEntity<ErrorResponse> handle(ImageLimitExceededException ex) {
        return ResponseEntity.status(422)
                .body(new ErrorResponse("IMAGE_LIMIT_EXCEEDED", ex.getMessage()));
    }

    @ExceptionHandler(CannotRemovePrincipalImageException.class)
    ResponseEntity<ErrorResponse> handle(CannotRemovePrincipalImageException ex) {
        return ResponseEntity.status(422)
                .body(new ErrorResponse("CANNOT_REMOVE_PRINCIPAL_IMAGE", ex.getMessage()));
    }

    @ExceptionHandler(ProductCannotBeActivatedException.class)
    ResponseEntity<ErrorResponse> handle(ProductCannotBeActivatedException ex) {
        return ResponseEntity.status(422)
                .body(new ErrorResponse("PRODUCT_CANNOT_BE_ACTIVATED", ex.getMessage()));
    }

    @ExceptionHandler(VariantNotFoundException.class)
    ResponseEntity<ErrorResponse> handle(VariantNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("VARIANT_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(LastActiveVariantException.class)
    ResponseEntity<ErrorResponse> handle(LastActiveVariantException ex) {
        return ResponseEntity.status(422)
                .body(new ErrorResponse("LAST_ACTIVE_VARIANT", ex.getMessage()));
    }

    @ExceptionHandler(InsufficientStockException.class)
    ResponseEntity<ErrorResponse> handle(InsufficientStockException ex) {
        return ResponseEntity.status(422)
                .body(new ErrorResponse("INSUFFICIENT_STOCK", ex.getMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<ErrorResponse> handle(MaxUploadSizeExceededException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("FILE_TOO_LARGE", "O arquivo excede o tamanho máximo permitido de 5MB"));
    }

    @ExceptionHandler(CartItemNotFoundException.class)
    ResponseEntity<ErrorResponse> handle(CartItemNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("CART_ITEM_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(CartItemNotOwnedException.class)
    ResponseEntity<ErrorResponse> handle(CartItemNotOwnedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("CART_ITEM_NOT_OWNED", ex.getMessage()));
    }

    @ExceptionHandler(CartVariantNotFoundException.class)
    ResponseEntity<ErrorResponse> handle(CartVariantNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("CART_VARIANT_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(VariantInactiveException.class)
    ResponseEntity<ErrorResponse> handle(VariantInactiveException ex) {
        return ResponseEntity.status(422)
                .body(new ErrorResponse("VARIANT_INACTIVE", ex.getMessage()));
    }

    @ExceptionHandler(ProductInactiveException.class)
    ResponseEntity<ErrorResponse> handle(ProductInactiveException ex) {
        return ResponseEntity.status(422)
                .body(new ErrorResponse("PRODUCT_INACTIVE", ex.getMessage()));
    }

    @ExceptionHandler(VariantMissingDimensionsException.class)
    ResponseEntity<ErrorResponse> handle(VariantMissingDimensionsException ex) {
        return ResponseEntity.status(422)
                .body(new ErrorResponse("VARIANT_MISSING_DIMENSIONS", ex.getMessage()));
    }

    @ExceptionHandler(CartInsufficientStockException.class)
    ResponseEntity<ErrorResponse> handle(CartInsufficientStockException ex) {
        return ResponseEntity.status(422)
                .body(new ErrorResponse("CART_INSUFFICIENT_STOCK", ex.getMessage()));
    }

    @ExceptionHandler(StorageException.class)
    ResponseEntity<ErrorResponse> handle(StorageException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("STORAGE_ERROR", "Falha no armazenamento de arquivo"));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "Ocorreu um erro inesperado"));
    }
}
