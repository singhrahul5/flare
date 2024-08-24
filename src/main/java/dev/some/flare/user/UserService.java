package dev.some.flare.user;

import dev.some.flare.exception.NotAvailableException;
import dev.some.flare.jwt.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    final private UserRepository userRepository;
    final private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Wrong Credentials."));
    }

    public void registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername()))
            throw new NotAvailableException("An account already exists with the username " + registerRequest.getUsername());
        if (userRepository.existsByEmail(registerRequest.getEmail()))
            throw new NotAvailableException("An account already exists with the email address " + registerRequest.getEmail());

        Instant now = Instant.now();
        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .role(Role.USER)
                .joinedAt(now)
                .passwordUpdatedAt(now)
                .build();
        userRepository.save(user);
    }

    public Instant getPasswordUpdatedAt(String username) {
        return userRepository.findByUsername(username)
                .map(User::getPasswordUpdatedAt)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }
}
