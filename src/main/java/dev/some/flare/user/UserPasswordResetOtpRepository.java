package dev.some.flare.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface UserPasswordResetOtpRepository extends JpaRepository<UserPasswordResetOtp, Long> {

    Optional<UserPasswordResetOtp> findByUserAndOtpAndExpiresAtGreaterThanEqualAndIsUsedFalse(User user, String otp,
                                                                                              Instant expiresAt);
}