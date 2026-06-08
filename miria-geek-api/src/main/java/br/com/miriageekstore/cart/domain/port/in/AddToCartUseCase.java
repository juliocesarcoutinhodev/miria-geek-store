package br.com.miriageekstore.cart.domain.port.in;

public interface AddToCartUseCase {
    CartResult execute(AddToCartCommand command);
}
