package me.study.mylog.users.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import me.study.mylog.auth.config.SecurityConfig;
import me.study.mylog.auth.utils.JwtUtil;
import me.study.mylog.users.dto.UserDto;
import me.study.mylog.users.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.*;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.AbstractSecurityBuilder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureRestDocs
//@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false) // @WebMvcTest 로 시큐리티를 다루는 게 쉽지 않은 것 같다..ㅜ
@WebMvcTest(controllers = UserController.class)//,
        /*
        * https://stackoverflow.com/questions/47593537/disable-spring-security-config-class-for-webmvctest-in-spring-boot
        * */
//        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebSecurityConfigurer.class)},
//        excludeAutoConfiguration = {SecurityAutoConfiguration.class,
//                SecurityFilterAutoConfiguration.class,
//                OAuth2ClientAutoConfiguration.class,
//                OAuth2ResourceServerAutoConfiguration.class}) // 특정 클래스를 설정하는 경우에 자동으로 시큐리티가 설정되는데 이를 해제
//,
//        excludeFilters = {
//        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
//})
class UserControllerTest {
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private JwtUtil jwtUtil;

// 아래 클래스는 스프링 라이브러리에서 제공하는 클래스를 이용해 빈으로 등록하므로 따로 설정할 필요가 없는 듯 하다.
// 그에 반해 JwtUtil은 목 설정하지 않으면 에러 발생
/*
 * Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'oauth2SecurityFilterChain' defined in class path resource [org/springframework/boot/autoconfigure/security/oauth2/client/servlet/OAuth2WebSecurityConfiguration$OAuth2SecurityFilterChainConfiguration.class]: Unsatisfied dependency expressed through method 'oauth2SecurityFilterChain' parameter 0; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.springframework.security.config.annotation.web.configuration.HttpSecurityConfiguration.httpSecurity' defined i
 * 와 같은 에러 발생
 * Oauth2.0 로직 추가 후 발생, 자동 설정 제외 클래스에 추가 지정해보려고 했지만, 계속 오류 발생함
 * */
//    @MockBean <= 관련해서 ApplicationContext 로드 오류 발생, 의존성 문제, mockbean을 쓰면 스프링 컨텍스트 활용하므로 생기는 이슈
//    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @BeforeEach
    void setUp() {
        log.info("---------------- setUP 실행 ------------------");
        objectMapper =  JsonMapper.builder().disable(MapperFeature.USE_ANNOTATIONS).build();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    }

    @DisplayName("새로운_회원_등록_201_성공_필요")
    @Test
    void registerNewUser() throws Exception {
        // given
        UserDto dto = UserDto.builder()
                .email("test@example.com")
                .name("hanah")
                .nickname("hanah")
                .password("1234")
                .build();

        String content = objectMapper.writeValueAsString(dto);
        String location = "http://localhost:8080/users/me"; // 포트 정보가 보이지 않음.

        //given(userService.register(any())).willReturn(response); 중첩 구조여서 에러 발생
        BDDMockito.willReturn(dto).given(userService).register(any());

        // when, then
        mockMvc.perform(post("/api/v1/auth/signup")
                       // .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status()
                        .isCreated())
                .andExpect(header()
                        .string("location", location))
                .andDo(print())
                .andDo(document("user-register",
                        requestFields(
                                fieldWithPath("email").description("User 이메일"),
                                fieldWithPath("name").description("User 고유한 명칭").optional(),
                                fieldWithPath("nickname").description("User 별칭").optional(),
                                fieldWithPath("password").description("User 비밀번호")
                    )
        ));
    }
}