package br.com.miriageekstore.identity.domain.port.in;

public interface ListUsersUseCase {
    ListUsersResult execute(ListUsersQuery query);
}
