package dev.some.flare.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = {
        @Index(name = "idx_username", columnList = "username", unique = true),
        @Index(name = "idx_email", columnList = "email", unique = true)
})
public class User implements UserDetails {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Instant joinedAt;

    @Column(nullable = false)
    private Instant passwordUpdatedAt;

    @Column(nullable = false)
    private Role role;

    @Builder
    public User(String username, String email, String password, Instant joinedAt, Instant passwordUpdatedAt,
                Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.joinedAt = joinedAt;
        this.passwordUpdatedAt = passwordUpdatedAt;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
