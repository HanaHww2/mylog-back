package me.study.mylog.auth.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.study.mylog.auth.oauth.OAuth2UserInfo;
import me.study.mylog.auth.oauth.OAuth2UserInfoFactory;
import me.study.mylog.common.exception.OAuthProcessingException;
import me.study.mylog.users.domain.AuthProviderType;
import me.study.mylog.users.domain.User;
import me.study.mylog.users.repository.UserRepository;
import me.study.mylog.users.service.UserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        // 써드파티에 OAuth2UserRequest 를 보내고 받은 응답값에 있는 Access Token으로 유저정보 get
        OAuth2User oAuth2User = delegate.loadUser(oAuth2UserRequest);

        return process(oAuth2UserRequest, oAuth2User);
    }

    // TODO readonly 트랜잭션 전파 체크해보기 (https://code-mania.tistory.com/m/143)
    // 획득한 유저정보를 Java Model과 매핑하고 프로세스 진행
    @Transactional
    OAuth2User process(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        AuthProviderType authProviderType = AuthProviderType.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase());

        // 이왕 팩토리패턴을 적용하니, 인터페이스를 사용
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(authProviderType, oAuth2User.getAttributes());

        if (userInfo.getEmail().isEmpty()) {
            throw new OAuthProcessingException("Email not found from OAuth2 provider");
        }
        // 유저 정보 조회
        Optional<User> userOptional = userRepository.findByEmail(userInfo.getEmail());
        User user;

        if (userOptional.isPresent()) {	// 이미 가입된 경우
            user = userOptional.get();
            if (authProviderType.equals(user.getAuthProviderType())) {
                throw new OAuthProcessingException("Wrong Match Auth Provider");
            }
        } else { // 가입되지 않은 경우
            user = userService.registerForOauth2(userInfo);
        }
        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }


}