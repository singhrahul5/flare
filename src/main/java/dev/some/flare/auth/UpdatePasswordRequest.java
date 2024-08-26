package dev.some.flare.auth;

import dev.some.flare.validation.Password;
import lombok.Data;

@Data
public class UpdatePasswordRequest {
    @Password
    private final String currentPassword;

    @Password
    private final String newPassword;
}
