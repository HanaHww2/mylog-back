package me.study.mylog.users.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import me.study.mylog.auth.config.SecurityConfig;
import me.study.mylog.auth.utils.JwtUtil;
import me.study.mylog.post.domain.Post;
import me.study.mylog.users.dto.UserDto;
import me.study.mylog.users.service.UserService;
import me.study.mylog.util.PostFixtureFactory;
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

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
@AutoConfigureMockMvc(addFilters = false) // @WebMvcTest ??? ??????????????? ????????? ??? ?????? ?????? ??? ??????..???
@WebMvcTest(controllers = UserController.class)//,
        /*
        * https://stackoverflow.com/questions/47593537/disable-spring-security-config-class-for-webmvctest-in-spring-boot
        * */
//        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebSecurityConfigurer.class)},
//        excludeAutoConfiguration = {SecurityAutoConfiguration.class,
//                SecurityFilterAutoConfiguration.class,
//                OAuth2ClientAutoConfiguration.class,
//                OAuth2ResourceServerAutoConfiguration.class}) // ?????? ???????????? ???????????? ????????? ???????????? ??????????????? ??????????????? ?????? ??????
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

// ?????? ???????????? ????????? ????????????????????? ???????????? ???????????? ????????? ????????? ??????????????? ?????? ????????? ????????? ?????? ??? ??????.
// ?????? ?????? JwtUtil??? ??? ???????????? ????????? ?????? ??????
/*
 * Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'oauth2SecurityFilterChain' defined in class path resource [org/springframework/boot/autoconfigure/security/oauth2/client/servlet/OAuth2WebSecurityConfiguration$OAuth2SecurityFilterChainConfiguration.class]: Unsatisfied dependency expressed through method 'oauth2SecurityFilterChain' parameter 0; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.springframework.security.config.annotation.web.configuration.HttpSecurityConfiguration.httpSecurity' defined i
 * ??? ?????? ?????? ??????
 * Oauth2.0 ?????? ?????? ??? ??????, ?????? ?????? ?????? ???????????? ?????? ?????????????????? ?????????, ?????? ?????? ?????????
 * */
//    @MockBean <= ???????????? ApplicationContext ?????? ?????? ??????, ????????? ??????, mockbean??? ?????? ????????? ???????????? ??????????????? ????????? ??????
//    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @BeforeEach
    void setUp() {
        log.info("---------------- setUP ?????? ------------------");
        objectMapper =  JsonMapper.builder().disable(MapperFeature.USE_ANNOTATIONS).build();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    }

    @DisplayName("?????????_??????_??????_201_??????_??????")
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
        String location = "http://localhost:8080/users/me"; // ?????? ????????? ????????? ??????.

        //given(userService.register(any())).willReturn(response); ?????? ???????????? ?????? ??????
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
                                fieldWithPath("email").description("User ?????????"),
                                fieldWithPath("name").description("User ????????? ??????").optional(),
                                fieldWithPath("nickname").description("User ??????").optional(),
                                fieldWithPath("password").description("User ????????????")
                    )
        ));
    }
}