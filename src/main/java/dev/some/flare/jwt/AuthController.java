package dev.some.flare.jwt;

import dev.some.flare.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerNewUser(@RequestBody @Valid RegisterRequest registerRequest) {
        userService.registerUser(registerRequest);
    }


    @PostMapping("/login")
    public JwtTokenResponse authenticateUser(@RequestBody JwtLoginRequest jwtLoginRequest) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                jwtLoginRequest.getUsername(),
                jwtLoginRequest.getPassword()
        );
        Authentication authentication = authenticationManager.authenticate(auth);
        return new JwtTokenResponse(jwtTokenService.generateJwtToken(authentication));
    }
}
