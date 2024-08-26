package dev.some.flare.auth;

import lombok.Data;

@Data
public class JwtTokenResponse {
    private final String token;
}
