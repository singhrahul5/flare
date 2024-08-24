package dev.some.flare.jwt;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JwtLoginRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
