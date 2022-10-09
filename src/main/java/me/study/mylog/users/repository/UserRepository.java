package me.study.mylog.users.repository;

import me.study.mylog.users.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByName(String name);

    @Query("SELECT ur.refreshToken FROM UserRefreshToken ur WHERE ur.user=:user")
    String getRefreshTokenById(User user);

    @Transactional
    @Modifying
    @Query("UPDATE UserRefreshToken ur SET ur.refreshToken=:token WHERE ur.user=:user") //ur.userId=:id")
    void updateRefreshToken(@Param("user") User user, @Param("token") String token);

}