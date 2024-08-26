package dev.some.flare.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import dev.some.flare.user.Role;
import dev.some.flare.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@EnableMethodSecurity
@Configuration
public class JwtSecurityConfig {

    private final static Logger logger = LoggerFactory.getLogger(JwtSecurityConfig.class);
    private final static String ROLE_PREFIX = "ROLE_";
    private final byte[] jwtSecretKey;

    public JwtSecurityConfig(@Value("${app.security.jwt-secret-key}") String secretKey) {
        logger.debug("app.security.jwt-secret-key = {}", secretKey);
        jwtSecretKey = Base64.getDecoder().decode(secretKey);
        logger.debug("app.security.jwt-secret-key byte array length = {}", jwtSecretKey.length);
    }

    @Bean
    static RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role(Role.ADMIN.name()).implies(Role.USER.name())
                .build();
    }

    // and, if using pre-post method security also add
    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setDefaultRolePrefix(ROLE_PREFIX);
        logger.debug("role prefix for user role {}", ROLE_PREFIX);
        expressionHandler.setRoleHierarchy(roleHierarchy);
        return expressionHandler;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthorityPrefix(ROLE_PREFIX);
        logger.debug("Scope prefix for jwt token {}", ROLE_PREFIX);
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(converter);
        return jwtAuthenticationConverter;
    }

    @Bean
    JwtDecoder jwtDecoder(final UserService userService) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                .withSecretKey(new SecretKeySpec(jwtSecretKey, "HS256"))
                .macAlgorithm(MacAlgorithm.HS256).build();

        jwtDecoder.setJwtValidator(jwt -> {
            String username = jwt.getSubject();
            Instant tokenIssuedAt = jwt.getIssuedAt();

            if (username != null && tokenIssuedAt != null) {
                // Fetch the user's last password update timestamp from your service
                Instant lastPasswordUpdate = userService.getPasswordUpdatedAt(username);

                if (lastPasswordUpdate != null && tokenIssuedAt.isBefore(lastPasswordUpdate)) {
                    OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN,
                            "The " + jwt + " claim is not valid", null);
                    logger.debug("The {} claim is not valid", jwt);
                    return OAuth2TokenValidatorResult.failure(error);
                }
            }

            return OAuth2TokenValidatorResult.success();
        });
        return jwtDecoder;
    }

    @Bean
    JwtEncoder jwtEncoder() {
//        OctetSequenceKey jwk = new OctetSequenceKey.Builder(jwtSecretKey)
//                .algorithm(JWSAlgorithm.HS256)
//                .build();
//        JWKSet jwkSet = new JWKSet(jwk);
//        return new NimbusJwtEncoder((jwkSelector, securityContext) -> jwkSelector.select(jwkSet));
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey));
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        String encodingId = "bcrypt";
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(encodingId, new BCryptPasswordEncoder());
        return new DelegatingPasswordEncoder(encodingId, encoders);
    }

    //to use AuthenticationManager
    @Bean
    ProviderManager providerManager(UserService userService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(passwordEncoder);
        provider.setUserDetailsService(userService);
        return new ProviderManager(provider);
    }
}
