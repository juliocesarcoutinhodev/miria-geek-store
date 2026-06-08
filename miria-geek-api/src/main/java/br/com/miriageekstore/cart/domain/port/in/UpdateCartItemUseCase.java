package br.com.miriageekstore.cart.domain.port.in;

public interface UpdateCartItemUseCase {
    CartResult execute(UpdateCartItemCommand command);
}
