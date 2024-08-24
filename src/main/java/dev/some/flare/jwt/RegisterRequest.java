package dev.some.flare.jwt;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequest {

    @Size(min = 1, max = 20, message = "Username length should be greater than equal to 1 and less then equal to 20")
    @Pattern(regexp = "[a-z_][a-z0-9_]{0,19}", message = "the username should starts with underscore or small letter alphabet and it can only contains small alphabet and underscore and numeric character.")
    private String username;

    @Size(min = 8, max = 100, message = "Password length should be greater than equal to 8 and less then equal to 100")
    private String password;

    @NotBlank
    @Email(message = "Invalid email address.")
    private String email;
}
