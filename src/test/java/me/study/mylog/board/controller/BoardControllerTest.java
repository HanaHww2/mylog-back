package me.study.mylog.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.study.mylog.board.service.BoardMemberReadService;
import me.study.mylog.board.service.BoardReadService;
import me.study.mylog.usecase.CreateBoardUsecase;
import me.study.mylog.usecase.DeleteBoardUsecase;
import me.study.mylog.usecase.ModifyBoardUsecase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@AutoConfigureRestDocs
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@SpringBootTest
class BoardControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private CreateBoardUsecase createBoardUsecase;
    @MockBean
    private ModifyBoardUsecase modifyBoardUsecase;
    @MockBean
    private DeleteBoardUsecase deleteBoardUsecase;
    @MockBean
    private BoardReadService boardReadService;
    @MockBean
    private BoardMemberReadService boardMemberReadService;

    @Test
    void getBoardById() {
    }

    @Test
    void getBoardsByUri() {
    }

    @Test
    void getBoardsByUser() {
    }

    @Test
    void createNewBoard() {
    }

    @Test
    void modifyBoard() {
    }

    @Test
    void deleteBoard() {
    }
}