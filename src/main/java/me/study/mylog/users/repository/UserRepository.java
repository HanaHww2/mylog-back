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

    @Query("SELECT ur.refreshToken FROM UserRefreshToken ur WHERE ur.userId=:id")
    String getRefreshTokenById(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("UPDATE UserRefreshToken ur SET ur.refreshToken=:token WHERE ur.userId=:id")
    void updateRefreshToken(@Param("id") Long id, @Param("token") String token);

}