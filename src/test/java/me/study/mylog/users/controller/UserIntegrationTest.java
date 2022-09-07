package me.study.mylog.users.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.study.mylog.DatabaseCleanUp;
import me.study.mylog.users.dto.SigninReqDto;
import me.study.mylog.users.dto.UserDto;
import me.study.mylog.users.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@SpringBootTest
@RequiredArgsConstructor
class UserIntegrationTest {

    private final MockMvc mockMvc;
    private final UserService userService;
    private DatabaseCleanUp databaseCleanUp;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        log.debug("---------------- setUP 실행 ------------------");
        userService.register(UserDto
                .builder()
                .email("test@example.com")
                .name("테스트")
                .password("1234")
                .build());

        // JsonProperty.Access.WRITE_ONLY 혹은 JsonIgnore와 같은 접근 제한 해제를 위해
        //objectMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
        objectMapper =  JsonMapper.builder().disable(MapperFeature.USE_ANNOTATIONS).build();
        // deprecated 옵션이 있어서, 다른 해결법을 생각해봐도 좋을 것 같다.
        // -> 사실 test 용 dto 를 내부 클래스로 두는 게 빠를 듯ㅋ

        // com.fasterxml.jackson.databind.exc.InvalidDefinitionException:
        // No serializer found for class me.study.userservice.user.dto.UserDto$UserDtoBuilder and no properties discovered to create BeanSerializer
        // (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS)
        // 아래의 코드로 해결, serializable을 상속받지 않아서 생기는 오류 같음.
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.afterPropertiesSet();
        databaseCleanUp.execute();
    }

    @DisplayName("권한이_필요한_url_호출_200_성공_필요")
    @Test
    @WithMockUser(username = "test@example.com")
    void callAuthorizedApi_shouldSucceedWith200() throws Exception {
        String expectedEmail = "test@example.com";
        String expectedRole = "USER";

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(expectedEmail))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value(expectedRole))
                .andDo(print());
    }

    @DisplayName("새로운_회원_등록_201_성공_필요")
    @Test
    void registerNewUser_shouldSucceedWith200() throws Exception {

        // given
        String content = objectMapper.writeValueAsString(UserDto.builder()
                .email("hanah@example.com")
                .password("1234")
                .name("하나")
                .build());

        String location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/users/me")
                .build()
                .toString();

        mockMvc.perform(post("/api/v1/auth/signup")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status()
                        .isCreated())
                .andExpect(header()
                        .string("location", location))
                .andDo(print());
    }

    @DisplayName("회원_로그인_수행하여_JWT_획득_및_200_성공_필요")
    @Test
    void signInUserAndGetJwt_shouldSucceedWith200() throws Exception {
        // given
        String content = objectMapper.writeValueAsString(SigninReqDto.builder()
                .email("test@example.com")
                .password("1234")
                .build());

        String expectedEmail = "test@example.com";
        String expectedRole = "USER";

        mockMvc.perform(post("/api/v1/auth/signin")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andDo(print());
    }

    //TODO 중복회원 발생시 오류 핸들링 결과가 잘 반환되는지 테스트

    //TODO 회원 로그인 실패시 결과 테스트

    //TODO 접근권한이 없는 URI에 접근할 때 오류 제어 테스트
}