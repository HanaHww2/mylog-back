package me.study.mylog.users.service;

import jdk.jfr.Registered;
import lombok.RequiredArgsConstructor;
import me.study.mylog.board.service.BoardReadService;
import me.study.mylog.board.service.BoardWriteService;
import me.study.mylog.common.exception.DuplicatedResoureException;
import me.study.mylog.users.domain.User;
import me.study.mylog.users.dto.UserDto;
import me.study.mylog.users.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserReadService {

    private final UserRepository userRepository;

    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("찾는 사용자가 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Not Registered User Mail"));

    }

    @Transactional(readOnly = true)
    public UserDto getUserDtoByEmail(String email) {
        var user = this.findUserByEmail(email);
        return UserDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .build();
    }


    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);

    }

    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return userRepository.existsByName(name);
    }

    @Transactional(readOnly = true)
    public boolean checkIfDuplicatedUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            throw new DuplicatedResoureException(
                    "Already Exists User Mail with " + user.get().getAuthProviderType(), HttpStatus.CONFLICT);
        }
        return false;
    }
}
