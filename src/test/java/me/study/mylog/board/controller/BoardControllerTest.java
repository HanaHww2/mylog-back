package me.study.mylog.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.study.mylog.board.dto.BoardDetailResponse;
import me.study.mylog.board.dto.CreateBoardRequest;
import me.study.mylog.board.dto.ModifyBoardRequest;
import me.study.mylog.board.entity.BoardMemberType;
import me.study.mylog.board.entity.BoardType;
import me.study.mylog.board.service.BoardMemberReadService;
import me.study.mylog.board.service.BoardReadService;
import me.study.mylog.usecase.CreateBoardUsecase;
import me.study.mylog.usecase.DeleteBoardUsecase;
import me.study.mylog.usecase.ModifyBoardUsecase;
import me.study.mylog.util.security.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @WithMockCustomUser
    @DisplayName("보드를 잘 생성하는지 검증")
    @Test
    void createNewBoard() throws Exception {
        // given
        CreateBoardRequest requestDto = CreateBoardRequest.builder()
                .name("new_board")
                .boardType(BoardType.DEFAULT)
                .icon("👏")
                .nickname("hanah")
                .build();

        BoardDetailResponse responseDto = BoardDetailResponse.builder()
                .id(14L)
                .name("new_board")
                .uri("new_board")
                .icon("👏")
                .categories(Collections.emptyList())
                .views(0L)
                .boardMemberType(BoardMemberType.MANAGER)
                .nickname("hanah")
                .build();

        String content = objectMapper.writeValueAsString(requestDto);

        given(createBoardUsecase.execute(any(), any())).willReturn(responseDto);

        // when, then
        mockMvc.perform(post("/api/v1/boards")
                        .content(content)
                        .header("Authorization", "Bearer {ACCESS_TOKEN}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("create-board",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name").description("Board 이름"),
                                fieldWithPath("boardType").description("Board 타입"),
                                fieldWithPath("icon").description("Board 아이콘").optional(),
                                fieldWithPath("nickname").description("사용자가 게시판에서 활용할 닉네임")
                        ),
                        responseFields(
                                fieldWithPath("message").description("응답 메세지"),
                                subsectionWithPath("result").description("결과 데이터"),
                                fieldWithPath("result.id").description("Board 아이디"),
                                fieldWithPath("result.name").description("Board 이름"),
                                fieldWithPath("result.uri").description("Board uri"),
                                fieldWithPath("result.views").description("Board views"),
                                fieldWithPath("result.boardMemberType").description("Board 멤버 타입"),
                                fieldWithPath("result.nickname").description("Board 멤버 닉네임"),
                                fieldWithPath("result.icon").description("Board 아이콘").optional(),
                                fieldWithPath("result.categories").description("Board의 Category 리스트").optional()
                        )
                ));
    }

    @WithMockCustomUser
    @DisplayName("보드를 잘 수정하는지 검증")
    @Test
    void modifyBoard() throws Exception {
        // given
        ModifyBoardRequest requestDto = ModifyBoardRequest.builder()
                .boardId(14L)
                .name("modified_board")
                .boardType(BoardType.GROUP_BOARD)
                .icon("👏")
                .build();

        BoardDetailResponse responseDto = BoardDetailResponse.builder()
                .id(14L)
                .name("modified_board")
                .uri("modified_board")
                .icon("👏")
                .categories(Collections.emptyList())
                .views(0L)
                .boardMemberType(BoardMemberType.MANAGER)
                .nickname("hanah")
                .build();

        String content = objectMapper.writeValueAsString(requestDto);

        given(modifyBoardUsecase.execute(any(), any())).willReturn(responseDto);

        // when, then
        mockMvc.perform(put("/api/v1/boards")
                        .content(content)
                        .header("Authorization", "Bearer {ACCESS_TOKEN}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("modify-board",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("boardId").description("Board 아이디"),
                                fieldWithPath("name").description("Board 이름"),
                                fieldWithPath("boardType").description("Board 타입").optional(),
                                fieldWithPath("icon").description("Board 아이콘")
                        ),
                        responseFields(
                                fieldWithPath("message").description("응답 메세지"),
                                subsectionWithPath("result").description("결과 데이터"),
                                fieldWithPath("result.id").description("Board 아이디"),
                                fieldWithPath("result.name").description("Board 이름"),
                                fieldWithPath("result.uri").description("Board uri"),
                                fieldWithPath("result.views").description("Board views"),
                                fieldWithPath("result.boardMemberType").description("Board 멤버 타입"),
                                fieldWithPath("result.nickname").description("Board 멤버 닉네임"),
                                fieldWithPath("result.icon").description("Board 아이콘").optional(),
                                fieldWithPath("result.categories").description("Board의 Category 리스트").optional()
                        )
                ));
    }

    @WithMockCustomUser
    @DisplayName("보드를 잘 삭제하는지 검증")
    @Test
    void deleteBoard() throws Exception {

        // when, then
        mockMvc.perform(delete("/api/v1/boards/{boardId}", 14L)
                        .header("Authorization", "Bearer {ACCESS_TOKEN}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("delete-board",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("boardId").description("보드 아이디")
                        ),
                        responseFields(
                                fieldWithPath("message").description("응답 메세지"),
                                subsectionWithPath("result").description("결과 데이터")
                        )
                ));
    }

    @WithMockCustomUser
    @DisplayName("아이디에 따라 보드를 잘 조회하는지 검증")
    @Test
    void getBoardById() throws Exception {
        // given
        BoardDetailResponse dto = BoardDetailResponse.builder()
                .id(13L)
                .name("test_board")
                .uri("test-board-uri")
                .icon("👏")
                .categories(Collections.emptyList())
                .views(145L)
                .boardMemberType(BoardMemberType.MANAGER)
                .nickname("hanah")
                .build();
        given(boardReadService.getBoardById(any())).willReturn(dto);

        // when, then
        mockMvc.perform(get("/api/v1/boards/{boardId}", 13L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status()
                        .isOk())
                .andDo(print())
                .andDo(document("get-board-info-by-id",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("boardId").description("보드 아이디")
                        ),
                        responseFields(
                                fieldWithPath("message").description("응답 메세지"),
                                subsectionWithPath("result").description("결과값"),
                                fieldWithPath("result.id").description("게시판 아이디"),
                                fieldWithPath("result.name").description("게시판명"),
                                fieldWithPath("result.uri").description("게시판 URI"),
                                fieldWithPath("result.views").description("게시판 조회수"),
                                fieldWithPath("result.icon").description("게시판 아이콘"),
                                fieldWithPath("result.categories").description("카테고리 리스트"),
                                fieldWithPath("result.nickname").description("유저 닉네임"),
                                fieldWithPath("result.boardMemberType").description("유저 역할")
                        )
                ));
    }

    @WithMockCustomUser
    @DisplayName("유저별 보드 리스트를 잘 조회하는지 검증")
    @Test
    void getBoardsByUser() throws Exception {
        // given
        BoardDetailResponse dto = BoardDetailResponse.builder()
                .id(13L)
                .name("test_board")
                .uri("test-board-uri")
                .icon("👏")
                .categories(Collections.emptyList())
                .views(145L)
                .boardMemberType(BoardMemberType.MANAGER)
                .nickname("hanah")
                .build();

        given(boardMemberReadService.getBoardsByUserId(any())).willReturn(List.of(dto));

        // when, then
        mockMvc.perform(get("/api/v1/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer {ACCESS_TOKEN}"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-board-list-by-user",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("message").description("응답 메세지"),
                                subsectionWithPath("result[]").description("결과값"),
                                fieldWithPath("result[].id").description("게시판 아이디"),
                                fieldWithPath("result[].name").description("게시판명"),
                                fieldWithPath("result[].uri").description("게시판 URI"),
                                fieldWithPath("result[].categories").description("카테고리 리스트"),
                                fieldWithPath("result[].icon").description("게시판 아이콘")
                        )
                ));
    }

    @WithMockCustomUser
    @DisplayName("URI 값으로 보드를 잘 조회하는지 검증")
    @Test
    void getBoardByUri() throws Exception {
        // given
        BoardDetailResponse dto = BoardDetailResponse.builder()
                .id(13L)
                .name("test_board")
                .uri("test-board-uri")
                .icon("👏")
                .categories(Collections.emptyList())
                .views(145L)
                .boardMemberType(BoardMemberType.MANAGER)
                .nickname("hanah")
                .build();

        given(boardReadService.getBoardByUri(any())).willReturn(dto);

        // when, then
        mockMvc.perform(get("/api/v1/boards/@{boardUri}", dto.getUri())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-board-info-by-uri",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("message").description("응답 메세지"),
                                subsectionWithPath("result").description("결과값"),
                                fieldWithPath("result.id").description("게시판 아이디"),
                                fieldWithPath("result.name").description("게시판명"),
                                fieldWithPath("result.uri").description("게시판 URI"),
                                fieldWithPath("result.views").description("게시판 조회수"),
                                fieldWithPath("result.icon").description("게시판 아이콘"),
                                fieldWithPath("result.categories").description("카테고리 리스트"),
                                fieldWithPath("result.nickname").description("유저 닉네임"),
                                fieldWithPath("result.boardMemberType").description("유저 역할")
                        )
                ));
    }

}