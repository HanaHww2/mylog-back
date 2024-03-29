package me.study.mylog.auth.security;

import lombok.RequiredArgsConstructor;
import me.study.mylog.common.exception.BadRequestException;
import me.study.mylog.users.domain.UserStatus;
import me.study.mylog.users.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        Optional<UserPrincipal> userPrincipal = userRepository.findByEmail(email)
                .map(user -> {
                    if (user.getStatus() == UserStatus.NORMAL) {
                        throw new BadRequestException(email + "-> 활성화되어 있지 않습니다.");
                    } else {
                        return UserPrincipal.create(user);
                    }
                });
        return userPrincipal.orElseThrow(() -> new UsernameNotFoundException(email + "-> 데이터베이스에서 찾을 수 없습니다."));
    }
}
