package me.study.mylog.users.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import me.study.mylog.common.exception.DuplicatedResoureException;
import me.study.mylog.auth.utils.JwtUtil;
import me.study.mylog.common.dto.ApiResponse;
import me.study.mylog.users.dto.SigninReqDto;
import me.study.mylog.users.dto.SigninResDto;
import me.study.mylog.users.dto.UserDto;
import me.study.mylog.users.dto.UserValidationDto;
import me.study.mylog.users.service.UserReadService;
import me.study.mylog.users.service.UserWriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.EnumSet;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserWriteService userWriteService;
    private final UserReadService userReadService;

    @PostMapping("/auth/signup")
    public ResponseEntity<ApiResponse<?>> signUpNewUser(@Valid @RequestBody UserDto userDto) {

        UserDto newUserDto;
        try {
            newUserDto = userWriteService.register(userDto);
        } catch (DuplicatedResoureException e) {
            e.printStackTrace();
            throw e;
        }
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath() //.fromContextPath(request)
                .path("/users/me")
                .build()
                .toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse<>("User Registered Successfully", newUserDto));

    }

    // TODO 수정 겟 매핑 방식으로, url 파람
    @GetMapping("/auth/duplicatedEmail")
    public ResponseEntity<ApiResponse<?>> checkIfDuplicatedEmail(@Valid @RequestBody UserValidationDto dto) {
        String email = dto.getEmail();
        boolean result = userReadService.checkIfDuplicatedUserByEmail(email); // 항상 false
        return ResponseEntity.ok()
                .body(new ApiResponse<>("Able to use", email));
    }
    
    // TODO 수정 겟 매핑 방식으로, url 파람
    @GetMapping("/auth/duplicatedName")
    public ResponseEntity<ApiResponse<?>> checkIfDuplicatedName(@Valid @RequestBody UserValidationDto dto) {
        String name = dto.getName();
        if (userReadService.existsByName(name)) {
            throw new DuplicatedResoureException("Not able to use", HttpStatus.CONFLICT);
        }
        return ResponseEntity.ok()
                .body(new ApiResponse<>("Able to use", name));
    }


    @PostMapping(value = "/auth/signin")
    public ResponseEntity<ApiResponse<SigninResDto>> signIn(@Valid @RequestBody SigninReqDto signinReqDto) {

        // 인증 매니저를 통해서 입력받은 아이디와 비밀번호를 바탕으로 사용자 정보를 검증한다.
        // 아래와 같이 기본 아이디/비밀번호 인증의 경우에는
        // DaoAuthenticationProvider가 디폴트로 동작하고, 이 때 커스텀해 둔 userDetailService의 loadUserByUsername()이 작동한다.
        // 즉, 최초 토큰을 발급받기 위해서는 db에서 user 정보를 가져와 인증을 수행한다.
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(
                new UsernamePasswordAuthenticationToken(
                        signinReqDto.getEmail(),
                        signinReqDto.getPassword()));

        log.info("{}", authentication.getPrincipal());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 헤더에 토큰을 담는다.
        // 경우에 따라 쿠키에 담거나, url 파라미터로 전송할 수도 있다.
        // 모바일 환경과 같이 쿠키 사용이 어려운 경우 후자를 활용한다고 한다. (헤더를 쓰는 게 낫지 않은지 확인해볼 필요가 있다.)
        String accessToken = jwtUtil.createAccessToken(authentication);
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.add(JwtUtil.AUTHORIZATION_HEADER, "Bearer " + accessToken);

        // 헤더보다는 body에 담는 것이 더 제너럴한 방식이라고 한다.
        // 게다가 클라이언트 스크립트에서는 기본적으로 header에 대한 접근을 막는 편인 것 같다.
        // 응답 바디에 값을 담아 전송할 수 있으니, 굳이 header에 대한 설정을 변경하지 않고 바디에 담아서 보내기로 결정했다. 그러나...
        // 시큐리티의 oauth success handling에서 기본적으로 리다이렉트 전략을 사용하고 있어서 많이들 쿠키에 토큰을  담아서 보내는 듯하다..
        // 응답을 보내는 방식을 통일하면 좋겠는데... 고민이 된다.
        SigninResDto signinResDto = SigninResDto.builder()
                .email(signinReqDto.getEmail())
                .Authorization("Bearer " + accessToken)
                .build();

        return ResponseEntity
                .ok()
                //.headers(httpHeaders)
                .body(new ApiResponse<>("Signed in Successfully", signinResDto));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/users/me")
    public ApiResponse<UserDto> getCurrentUserInfo(Principal principal) {
        UserDto userDto = userReadService.getUserDtoByEmail(principal.getName());
        return new ApiResponse<>("User Info getting by UserEmail", userDto);
    }
}
