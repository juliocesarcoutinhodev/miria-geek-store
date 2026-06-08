package br.com.miriageekstore.cart.infrastructure.adapter.in.web;

import br.com.miriageekstore.cart.domain.port.in.AddToCartCommand;
import br.com.miriageekstore.cart.domain.port.in.AddToCartUseCase;
import br.com.miriageekstore.cart.domain.port.in.GetCartUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Cart", description = "Cart management — requires authentication")
@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
class CartController {

    private final AddToCartUseCase addToCartUseCase;
    private final GetCartUseCase getCartUseCase;
    private final CartWebMapper mapper;

    @Operation(summary = "Get cart",
               security = @SecurityRequirement(name = "cookieAuth"))
    @GetMapping
    CartResponse getCart(@AuthenticationPrincipal Jwt jwt) {
        var userId = UUID.fromString(jwt.getSubject());
        return mapper.toResponse(getCartUseCase.execute(userId));
    }

    @Operation(summary = "Add item to cart",
               security = @SecurityRequirement(name = "cookieAuth"))
    @PostMapping("/items")
    CartResponse addItem(@AuthenticationPrincipal Jwt jwt,
                         @Valid @RequestBody AddToCartRequest request) {
        var userId  = UUID.fromString(jwt.getSubject());
        var command = new AddToCartCommand(userId, request.variantId(), request.quantity());
        return mapper.toResponse(addToCartUseCase.execute(command));
    }
}
