package me.study.mylog.users.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import me.study.mylog.common.exception.DuplicatedResoureException;
import me.study.mylog.auth.utils.JwtUtil;
import me.study.mylog.common.dto.CommonResponse;
import me.study.mylog.users.dto.SigninReqDto;
import me.study.mylog.users.dto.SigninResDto;
import me.study.mylog.users.dto.UserDto;
import me.study.mylog.users.dto.UserValidationDto;
import me.study.mylog.users.service.UserService;
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

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;

    @PostMapping("/auth/signup")
    public ResponseEntity<CommonResponse<?>> signUpNewUser(@Valid @RequestBody UserDto userDto) {
        UserDto newUserDto;
        try {
            newUserDto = userService.register(userDto);
        } catch (DuplicatedResoureException e) {
            e.printStackTrace();
            throw e;
        }
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath() //.fromContextPath(request)
                .path("/users/me")
                .build()
                .toUri();

        return ResponseEntity.created(location)
                .body(new CommonResponse<>("User Registered Successfully", newUserDto));

    }

    @PostMapping("/auth/duplicatedEmail")
    public ResponseEntity<CommonResponse<?>> checkIfDuplicatedEmail(@Valid @RequestBody UserValidationDto dto) {
        String email = dto.getEmail();
        boolean result = userService.checkIfDuplicatedUserByEmail(email); // 항상 false
        return ResponseEntity.ok()
                .body(new CommonResponse<>("can use", email));
    }

    @PostMapping("/auth/duplicatedName")
    public ResponseEntity<CommonResponse<?>> checkIfDuplicatedName(@Valid @RequestBody UserValidationDto dto) {
        String name = dto.getName();
        if (userService.existsByName(name)) {
            throw new DuplicatedResoureException("can't use", HttpStatus.CONFLICT);
        }
        return ResponseEntity.ok()
                .body(new CommonResponse<>("can use", name));
    }


    @PostMapping(value = "/auth/signin")
    public ResponseEntity<CommonResponse<SigninResDto>> signIn(@Valid @RequestBody SigninReqDto signinReqDto) {

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
        // 응답 바디에 값을 담아 전송할 수 있으니, 굳이 header에 대한 설정을 변경하지 않고 바디에 담아서 보내기로 결정했더,
        SigninResDto signinResDto = SigninResDto.builder()
                .email(signinReqDto.getEmail())
                .Authorization("Bearer " + accessToken)
                .build();

        return ResponseEntity
                .ok()
                //.headers(httpHeaders)
                .body(new CommonResponse<>("Signed in Successfully", signinResDto));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/users/me")
    public CommonResponse<UserDto> getCurrentUserInfo(Principal principal) {
        UserDto userDto = userService.findUserByEmail(principal.getName());
        return new CommonResponse<>("User Info getting by UserEmail", userDto);
    }
}
