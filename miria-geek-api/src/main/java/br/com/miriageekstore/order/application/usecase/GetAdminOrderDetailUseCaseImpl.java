package br.com.miriageekstore.order.application.usecase;

import br.com.miriageekstore.order.domain.exception.OrderNotFoundException;
import br.com.miriageekstore.order.domain.port.in.GetAdminOrderDetailResult;
import br.com.miriageekstore.order.domain.port.in.GetAdminOrderDetailUseCase;
import br.com.miriageekstore.order.domain.port.out.AdminOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetAdminOrderDetailUseCaseImpl implements GetAdminOrderDetailUseCase {

    private final AdminOrderRepository repository;

    @Override
    @Transactional(readOnly = true)
    public GetAdminOrderDetailResult execute(UUID id) {
        return repository.findByIdForAdmin(id)
                .orElseThrow(OrderNotFoundException::new);
    }
}
