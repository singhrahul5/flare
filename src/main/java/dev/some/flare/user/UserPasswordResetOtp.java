package dev.some.flare.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserPasswordResetOtp {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String otp;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean isUsed;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public UserPasswordResetOtp(String otp, Instant createdAt, Instant expiresAt, boolean isUsed, User user) {
        this.otp = otp;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.isUsed = isUsed;
        this.user = user;
    }
}
