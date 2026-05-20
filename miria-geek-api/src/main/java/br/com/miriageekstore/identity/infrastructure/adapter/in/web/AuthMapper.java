package br.com.miriageekstore.identity.infrastructure.adapter.in.web;

import br.com.miriageekstore.identity.domain.port.in.LoginCommand;
import br.com.miriageekstore.identity.domain.port.in.LoginResult;
import br.com.miriageekstore.identity.domain.port.in.RegisterUserCommand;
import br.com.miriageekstore.identity.domain.port.in.RegisterUserResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
interface AuthMapper {

    @Mapping(target = "rawPassword", source = "password")
    RegisterUserCommand toCommand(RegisterRequest request);

    LoginCommand toCommand(LoginRequest request, String ipAddress, String userAgent);

    RegisterResponse toResponse(RegisterUserResult result);

    LoginResponse toResponse(LoginResult result);
}
