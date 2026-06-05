package br.com.miriageekstore.order.domain.port.in;

public interface UpdateTrackingUseCase {
    UpdateTrackingResult execute(UpdateTrackingCommand command);
}
