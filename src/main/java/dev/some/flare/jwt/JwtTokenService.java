package dev.some.flare.jwt;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class JwtTokenService {
    private final static Logger logger = LoggerFactory.getLogger(JwtTokenService.class);

    private final JwtEncoder jwtEncoder;

    public String generateJwtToken(Authentication auth) {

        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
        Instant now = Instant.now();

        String scope = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(auth.getName())
                .issuedAt(now)
                .expiresAt(now.plus(30, ChronoUnit.DAYS))
                .issuer("self")
                .claim("scope", scope)
                .build();

        logger.debug("Authentication#getName() {}", auth.getName());
        logger.debug("Scope {}", scope);
        logger.debug("Authentication#getPrincipal() {}", auth.getPrincipal());
        logger.debug("Authentication#getCredentials() {}", auth.getCredentials());
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, jwtClaimsSet))
                .getTokenValue();
    }
}
