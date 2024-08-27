package dev.some.flare.auth.dto;

import lombok.Data;

@Data
public class JwtTokenResponse {
    private final String token;
}
