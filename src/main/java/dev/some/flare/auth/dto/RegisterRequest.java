package dev.some.flare.auth.dto;

import dev.some.flare.validation.Password;
import dev.some.flare.validation.Username;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequest {

    @Username
    private final String username;

    @Password
    private final String password;

    @Email
    private final String email;
}
