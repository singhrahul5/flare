package dev.some.flare.auth;

import dev.some.flare.validation.Password;
import dev.some.flare.validation.UsernameOrEmail;
import lombok.Data;

@Data
public class JwtLoginRequest {
    @UsernameOrEmail
    private final String username;
    @Password
    private final String password;
}
