package me.study.mylog.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.study.mylog.util.DatabaseCleanUp;
import me.study.mylog.util.InitDBService;
import me.study.mylog.auth.security.UserPrincipal;
import me.study.mylog.users.domain.RoleType;
import me.study.mylog.util.security.WithMockCustomUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@AutoConfigureRestDocs
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@SpringBootTest
class BoardIntegrationTest {

    //    @Autowired
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;
    private final InitDBService initDBService;
    private final DatabaseCleanUp databaseCleanUp;

    @BeforeEach
    void setUp() {
        log.debug("---------------- setUP 실행 ------------------");
        initDBService.initDBForIntegration();

    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.afterPropertiesSet();
        databaseCleanUp.execute();
    }

    @WithMockCustomUser
    @DisplayName("사용자가 멤버로 등록된 게시판 정보들을 돌려주는지 확인")
    @Test
    void getBoardsByUser() throws Exception {
        // given

        // when, then
        mockMvc.perform(get("/api/v1/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer {ACCESS_TOKEN}"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @WithMockCustomUser
    @DisplayName("주어진 게시판 아이디에 따라 게시판 정보를 돌려주는지 확인")
    @Test
    void getBoardById() throws Exception {
        // given

        // when, then
        mockMvc.perform(get("/api/v1/boards/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status()
                        .isOk())
                .andDo(print());
    }
}