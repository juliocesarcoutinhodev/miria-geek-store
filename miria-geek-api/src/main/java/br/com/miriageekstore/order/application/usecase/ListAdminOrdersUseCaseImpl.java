package br.com.miriageekstore.order.application.usecase;

import br.com.miriageekstore.order.domain.port.in.AdminOrderQuery;
import br.com.miriageekstore.order.domain.port.in.ListAdminOrdersResult;
import br.com.miriageekstore.order.domain.port.in.ListAdminOrdersUseCase;
import br.com.miriageekstore.order.domain.port.out.AdminOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListAdminOrdersUseCaseImpl implements ListAdminOrdersUseCase {

    private final AdminOrderRepository repository;

    @Override
    @Transactional(readOnly = true)
    public ListAdminOrdersResult execute(AdminOrderQuery query) {
        return repository.searchForAdmin(query);
    }
}
