package me.study.mylog.auth.config;

import lombok.RequiredArgsConstructor;
import me.study.mylog.auth.security.*;
import me.study.mylog.users.domain.RoleType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2CookieAuthorizationRequestRepository oAuth2CookieAuthorizationRequestRepository;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    /*
     * security 설정 시, 사용할 인코더 설정
     * */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용하지 않음, 토큰 방식
                .and()
                .csrf().disable() //ignoringAntMatchers("/h2-console/**") // h2-console 페이지가 csrf 대응이 되어있지 않으므로 예외로 둔다.
                .headers().frameOptions().disable()
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests((authz) -> authz
                            // TODO 아래 두 접근에 대해서 관리자만 접근 가능하도록 제한 설정을 해둘 필요성!
                            .antMatchers("/v3/api-docs", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                            .antMatchers("/actuator/health", "/h2-console/**", "/docs/**").permitAll()

                            .antMatchers("/", "/css/**").permitAll()
                            .antMatchers(HttpMethod.GET, "/api/v1/**").permitAll()
                            .antMatchers("/api/images/local/**", "/api/v1/auth/**").permitAll()

                            .antMatchers("/api/v1/users/**").hasRole(RoleType.USER.name())
                            //.antMatchers("/api/v1/boards", "/api/v1/boards/**").permitAll()
                            .anyRequest().authenticated()
                )
                .oauth2Login()
                .authorizationEndpoint()
                .authorizationRequestRepository(oAuth2CookieAuthorizationRequestRepository)
                .and()
                .userInfoEndpoint()
                //.userService(customOAuth2UserService) 명시하지 않아도 빈에 등록되어 우선적으로 활용된다.
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)	// 401
                .accessDeniedHandler(jwtAccessDeniedHandler);		// 403;

        return http.build();
    }
}
