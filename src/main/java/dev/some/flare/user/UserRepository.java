package dev.some.flare.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameOrEmail(String username, String email);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Transactional
    @Modifying
    @Query("update User u set u.password = ?1, u.passwordUpdatedAt = ?2 where u.username = ?3")
    int updatePasswordAndPasswordUpdatedAtByUsername(String password, Instant passwordUpdatedAt, String username);


}
