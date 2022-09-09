package me.study.mylog.users.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.study.mylog.common.dto.CommonResult;
import me.study.mylog.users.dto.SigninReqDto;
import me.study.mylog.users.dto.UserDto;
import me.study.mylog.users.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
@RequiredArgsConstructor
class UserControllerTest {

    private final MockMvc mockMvc;
    @MockBean
    private final UserService userService;
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
        String location = "http://localhost:8080/users/me";

        //given(userService.register(any())).willReturn(response); 중첩 구조여서 에러 발생
        BDDMockito.willReturn(dto).given(userService).register(any());

        // when, then
        mockMvc.perform(post("/api/v1/auth/signup")
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