package br.com.miriageekstore.identity.domain.port.in;

import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.model.UserStatus;

public record UpdateUserStatusCommand(UserId targetUserId, UserStatus newStatus, UserId requesterId) {}
