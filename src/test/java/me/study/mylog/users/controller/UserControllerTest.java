package me.study.mylog.users.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.study.mylog.auth.config.SecurityConfig;
import me.study.mylog.auth.utils.JwtUtil;
import me.study.mylog.common.dto.CommonResult;
import me.study.mylog.users.dto.SigninReqDto;
import me.study.mylog.users.dto.UserDto;
import me.study.mylog.users.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@WebMvcTest(controllers = UserController.class, excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebSecurityConfigurer.class)},
        excludeAutoConfiguration = {SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                OAuth2ClientAutoConfiguration.class,
                OAuth2ResourceServerAutoConfiguration.class}) // 특정 클래스를 설정하는 경우에 자동으로 시큐리티가 설정되는데 이를 해제
        /*, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
}*/
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    private ObjectMapper objectMapper;

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
        String location = "http://localhost/users/me"; // 포트 정보가 보이지 않음.

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
                .andDo(print());
//                .andDo(document("user-register",
//                        requestFields(
//                                fieldWithPath("email").description("User 이메일"),
//                                fieldWithPath("name").description("User 고유한 명칭").optional(),
//                                fieldWithPath("nickname").description("User 별칭").optional(),
//                                fieldWithPath("password").description("User 비밀번호")
//                    )
//        ));
    }
}