package me.study.mylog.users.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.study.mylog.auth.oauth.OAuth2UserInfo;
import me.study.mylog.board.service.BoardWriteService;
import me.study.mylog.common.exception.DuplicatedResoureException;
import me.study.mylog.board.service.BoardReadService;
import me.study.mylog.users.domain.AuthProviderType;
import me.study.mylog.users.domain.User;
import me.study.mylog.users.domain.UserStatus;
import me.study.mylog.users.dto.UserDto;
import me.study.mylog.users.repository.UserRepository;
import me.study.mylog.users.domain.RoleType;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class UserWriteService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BoardWriteService boardWriteService;

    @Transactional
    public UserDto register(UserDto userDto) throws DuplicatedResoureException {

        // 이메일과 고유 명칭 2차 검증..?
        // (이미 검증하긴 했는데... 중간에 다른 사람이 그 아이디를 쓸 확률도 있긴 하겠지... 실제로는 어떻게 해결하는 거지?)
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new DuplicatedResoureException("Already Exists User Mail", HttpStatus.CONFLICT);
        }
        if (userRepository.existsByName(userDto.getName())) {
            throw new DuplicatedResoureException("Already Exists User Name", HttpStatus.CONFLICT);
        }

        User user = User.builder()
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .name(userDto.getName())
                .nickname(userDto.getNickname()!=null?userDto.getNickname():userDto.getName())
                .role(RoleType.USER)
                .authProviderType(AuthProviderType.DEFAULT)
                .status(UserStatus.NEED_VALIDATION)
                .build();
        log.info("{}", user);

        User newUser = userRepository.save(user);

        // TODO 보드와 보드멤버 테이블 관련해서 수정 필요
        // 사용자 기준 보드를 조회할 때, boardMember에서 사용자가 권한을 가진 모든 보드 아이디를 조회하여
        // 이를 바탕으로 board 정보 조회를 수행한다.
        boardWriteService.createFirstBoard(user);


        return UserDto.builder()
                .email(newUser.getEmail())
                .name(newUser.getName())
                .nickname(newUser.getNickname())
                .build();
    }

    @Transactional
    public User registerForOauth2(OAuth2UserInfo oAuth2UserInfo) throws DuplicatedResoureException {

        StringBuffer name = new StringBuffer(oAuth2UserInfo.getName());
        String tempName = name.toString();

        // TODO unique 이름 생성하기 고민, 유사한 이름 전부 조회해서 피하기?ㅠ, 트랜잭션 고민, 리트라이?
        while (userRepository.existsByName(tempName)) {
            tempName = name.append('-')
                    .append(UUID.randomUUID()) // 좀 더 짧게 랜덤화
                    .toString();
        }

        User user = oAuth2UserInfo.toEntity(tempName);
        log.info("{}", user);
        User newUser = userRepository.save(user);
        boardWriteService.createFirstBoard(user);

        return newUser;
    }
}
