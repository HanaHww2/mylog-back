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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Slf4j
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@SpringBootTest
@RequiredArgsConstructor
class UserIntegrationTest {

    private final MockMvc mockMvc;
    private final UserService userService;
    @Autowired
    private final DatabaseCleanUp databaseCleanUp;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        log.debug("---------------- setUP ?????? ------------------");
        userService.register(UserDto
                .builder()
                .email("test@example.com")
                .name("?????????")
                .password("1234")
                .build());

        // JsonProperty.Access.WRITE_ONLY ?????? JsonIgnore??? ?????? ?????? ?????? ????????? ??????
        //objectMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
        objectMapper =  JsonMapper.builder().disable(MapperFeature.USE_ANNOTATIONS).build();
        // deprecated ????????? ?????????, ?????? ???????????? ??????????????? ?????? ??? ??????.
        // -> ?????? test ??? dto ??? ?????? ???????????? ?????? ??? ?????? ???

        // com.fasterxml.jackson.databind.exc.InvalidDefinitionException:
        // No serializer found for class me.study.userservice.user.dto.UserDto$UserDtoBuilder and no properties discovered to create BeanSerializer
        // (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS)
        // ????????? ????????? ??????, serializable??? ???????????? ????????? ????????? ?????? ??????.
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.afterPropertiesSet();
        databaseCleanUp.execute();
    }

    @DisplayName("?????????_?????????_url_??????_200_??????_??????")
    @Test
    @WithMockUser(username = "test@example.com")
    void callAuthorizedApi_shouldSucceedWith200() throws Exception {
        String expectedEmail = "test@example.com";
        String expectedRole = "USER";

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value(expectedEmail))
      //          .andExpect(MockMvcResultMatchers.jsonPath("$.role").value(expectedRole))
                .andDo(print());
    }

    @DisplayName("?????????_??????_??????_201_??????_??????")
    @Test
    void registerNewUser_shouldSucceedWith200() throws Exception {

        // given
        String content = objectMapper.writeValueAsString(UserDto.builder()
                .email("hanah@example.com")
                .name("??????")
                .nickname("hanah")
                .password("1234")
                .build());

        String location = "http://localhost/users/me";

        log.debug("================{}==============", location);
        mockMvc.perform(post("/api/v1/auth/signup")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status()
                        .isCreated())
                .andExpect(header()
                        .string("location", location))
                .andDo(print());
    }

    @DisplayName("??????_?????????_????????????_JWT_??????_???_200_??????_??????")
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.authorization").exists())
                .andDo(print());
    }

    //TODO ???????????? ????????? ?????? ????????? ????????? ??? ??????????????? ?????????

    //TODO ?????? ????????? ????????? ?????? ?????????

    //TODO ??????????????? ?????? URI??? ????????? ??? ?????? ?????? ?????????
}