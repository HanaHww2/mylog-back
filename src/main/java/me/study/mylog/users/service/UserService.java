package me.study.mylog.users.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.study.mylog.auth.exception.DuplicateUserMailException;
import me.study.mylog.board.BoardService;
import me.study.mylog.users.domain.User;
import me.study.mylog.users.dto.UserDto;
import me.study.mylog.users.repository.UserRepository;
import me.study.mylog.users.domain.RoleType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BoardService boardService;

    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("찾는 사용자가 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User register(UserDto userDto) throws DuplicateUserMailException {
        if (userRepository.findByEmail(userDto.getEmail())
                .orElse(null) != null) {
            throw new DuplicateUserMailException("Already Exists User Mail");
        }

        User user = User.builder()
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .name(userDto.getName())
                .role(RoleType.USER)
                .build();
        log.info("{}", user);

        boardService.createFirstBoard(user);
        User newUser = userRepository.save(user);
        return newUser;
    }
}
