package dev.some.flare.auth;

import dev.some.flare.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

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
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(jwtLoginRequest.getUsername(), jwtLoginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(auth);
        return new JwtTokenResponse(jwtTokenService.generateJwtToken(authentication));
    }


    @PostMapping("/{username:[a-z_][a-z0-9_]{0,19}}/password")
    @PreAuthorize("hasRole('USER') and #username == authentication.name")
    public void updatePassword(@RequestBody @Valid UpdatePasswordRequest updatePasswordRequest,
                               @PathVariable String username, Authentication auth) {
        logger.debug("Username = {}", username);
        logger.debug("Authentication#getName() = {}", auth.getName());
        logger.debug("Authentication#getPrincipal() = {}", auth.getPrincipal());
        logger.debug("Authentication#getCredentials() = {}", auth.getCredentials());
        UsernamePasswordAuthenticationToken authenticate = new UsernamePasswordAuthenticationToken(username,
                updatePasswordRequest.getCurrentPassword());
        authenticationManager.authenticate(authenticate);
        userService.updatePassword(username, updatePasswordRequest.getNewPassword());
    }


    @PostMapping("/reset-password/request-otp")
    public void resetPasswordRequestOtp(@RequestBody @Valid ResetPasswordGenerateOtpRequest resetPasswordGenerateOtpRequest) {
        userService.generateOtpAndSendMail(resetPasswordGenerateOtpRequest.getUsername());
    }

    @PostMapping("/reset-password/verify-otp")
    public void resetPasswordVerifyOtp(@RequestBody @Valid ResetPasswordVerifyOtpRequest resetPasswordVerifyOtpRequest) {
        userService.verifyOtpAndUpdatePassword(resetPasswordVerifyOtpRequest);
    }
}
