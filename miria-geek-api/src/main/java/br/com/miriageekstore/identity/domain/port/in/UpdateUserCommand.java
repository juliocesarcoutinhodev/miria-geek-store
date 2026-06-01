package br.com.miriageekstore.identity.domain.port.in;

import br.com.miriageekstore.identity.domain.model.UserId;

public record UpdateUserCommand(
        UserId targetUserId,
        UserId adminId,
        String fullName,
        String email,
        String role
) {}
