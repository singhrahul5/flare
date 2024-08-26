package dev.some.flare.user;

import dev.some.flare.auth.RegisterRequest;
import dev.some.flare.auth.ResetPasswordVerifyOtpRequest;
import dev.some.flare.exception.InvalidOtpException;
import dev.some.flare.exception.NotAvailableException;
import dev.some.flare.utils.EmailService;
import dev.some.flare.utils.OtpHashService;
import dev.some.flare.utils.RandomIdGeneratorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserPasswordResetOtpRepository userPasswordResetOtpRepository;
    private final RandomIdGeneratorService randomIdGeneratorService;
    private final EmailService emailService;
    private final OtpHashService otpHashService;

    @Override
    public User loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
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

    public void updatePassword(String username, String password) {
        if (userRepository.updatePasswordAndPasswordUpdatedAtByUsername(passwordEncoder.encode(password),
                Instant.now(), username) != 1)
            throw new UsernameNotFoundException("User not found.");
    }

    @Transactional
    public void generateOtpAndSendMail(String usernameOrEmail) {
        // load user
        User user = loadUserByUsername(usernameOrEmail);
        // generate otp
        String otp = randomIdGeneratorService.generateOtp();

        // create otp entry in db
        Instant now = Instant.now();
        UserPasswordResetOtp userPasswordResetOtp = UserPasswordResetOtp.builder()
                .user(user)
                .otp(otpHashService.hashOtp(otp)) // hash otp then store
                .createdAt(now)
                .expiresAt(now.plus(30, ChronoUnit.MINUTES))
                .isUsed(false)
                .build();

        userPasswordResetOtpRepository.save(userPasswordResetOtp);

        // if all good then send email to the user
        emailService.sendPasswordResetMail(user.getEmail(), user.getUsername(), otp);
    }

    @Transactional
    public void verifyOtpAndUpdatePassword(ResetPasswordVerifyOtpRequest resetPasswordVerifyOtpRequest) {
        // load user
        User user = loadUserByUsername(resetPasswordVerifyOtpRequest.getUsername());

        // verify otp
        String hashedOtp = otpHashService.hashOtp(resetPasswordVerifyOtpRequest.getOtp()); // hash otp before find
        // operatiron
        UserPasswordResetOtp userPasswordResetOtp = userPasswordResetOtpRepository
                .findByUserAndOtpAndExpiresAtGreaterThanEqualAndIsUsedFalse(user, hashedOtp, Instant.now())
                .orElseThrow(() -> new InvalidOtpException("Invalid otp."));

        // set otp as used
        userPasswordResetOtp.setUsed(true);
        userPasswordResetOtpRepository.save(userPasswordResetOtp);

        //update password
        updatePassword(user.getUsername(), resetPasswordVerifyOtpRequest.getNewPassword());
    }
}
