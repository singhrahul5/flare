package dev.some.flare.auth;

import dev.some.flare.validation.UsernameOrEmail;
import lombok.Data;

@Data
public class ResetPasswordGenerateOtpRequest {
    @UsernameOrEmail
    private String username;
}
