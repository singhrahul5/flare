package dev.some.flare.auth;

import dev.some.flare.validation.Otp;
import dev.some.flare.validation.Password;
import dev.some.flare.validation.UsernameOrEmail;
import lombok.Data;

@Data
public class ResetPasswordVerifyOtpRequest {
    @UsernameOrEmail
    private final String username;

    @Otp
    private final String otp;

    @Password
    private final String newPassword;
}
