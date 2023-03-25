package me.study.mylog.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.study.mylog.category.dto.CategoryCreateRequest;
import me.study.mylog.category.dto.CategoryResponseDto;
import me.study.mylog.category.service.CategoryWriteService;
import me.study.mylog.comment.dto.CommentResponse;
import me.study.mylog.comment.dto.CreateCommentRequest;
import me.study.mylog.comment.dto.ModifyCommentRequest;
import me.study.mylog.comment.service.CommentWriteService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@AutoConfigureRestDocs
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@SpringBootTest
class CommentControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private CommentWriteService commentWriteService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createComment() throws Exception{
        // given
        CreateCommentRequest request = CreateCommentRequest.builder()
                .boardId(5L)
                .postId(60L)
                .writerName("writer1")
                .content("test-comments")
                .imageListDto(Collections.EMPTY_LIST)
                .parentCommentId(5L)
                .build();
        String content = objectMapper.writeValueAsString(request);

        CommentResponse response = CommentResponse.builder()
                .id(59L)
                .userId(4L)
                .boardId(5L)
                .postId(60L)
                .writerName("writer1")
                .content("test-comments")
                .parentCommentId(5L)
                .modifiedAt(LocalDateTime.now())
                .isDeleted(Boolean.FALSE)
                .build();

        given(commentWriteService.createNewComment(any(), any())).willReturn(response);

        // when, then
        mockMvc.perform(post("/api/v1/comments")
                        .content(content)
                        .header("Authorization", "Bearer {ACCESS_TOKEN}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("create-comments",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("boardId").description("Board 아이디"),
                                fieldWithPath("postId").description("Post 아이디"),
                                fieldWithPath("writerName").description("작성자명"),
                                fieldWithPath("content").description("Comments 내용"),
                                fieldWithPath("parentCommentId").description("부모 댓글 아이디"),
                                fieldWithPath("imageListDto").description("댓글 이미지 목록")
                        ),
                        responseFields(
                                fieldWithPath("message").description("응답 메세지"),
                                subsectionWithPath("result").description("결과 데이터"),
                                fieldWithPath("result.id").description("Comment 아이디"),
                                fieldWithPath("result.boardId").description("Board 아이디"),
                                fieldWithPath("result.postId").description("Post 아이디"),
                                fieldWithPath("result.userId").description("작성자 아이디"),
                                fieldWithPath("result.writerName").description("작성자명"),
                                fieldWithPath("result.content").description("Comments 내용"),
                                fieldWithPath("result.parentCommentId").description("부모 댓글 아이디"),
                                fieldWithPath("result.modifiedAt").description("수정일자"),
                                fieldWithPath("result.isDeleted").description("삭제 댓글")
                        )
                ));
    }

    @Test
    void modifyComment() throws Exception {
        // given
        ModifyCommentRequest request = ModifyCommentRequest.builder()
                .commentId(3L)
                .content("test-comments")
                .parentCommentId(5L)
                .build();
        String content = objectMapper.writeValueAsString(request);

        CommentResponse response = CommentResponse.builder()
                .id(59L)
                .userId(4L)
                .boardId(5L)
                .postId(60L)
                .writerName("writer1")
                .content("test-comments")
                .parentCommentId(5L)
                .modifiedAt(LocalDateTime.now())
                .isDeleted(Boolean.FALSE)
                .build();

        given(commentWriteService.createNewComment(any(), any())).willReturn(response);

        // when, then
        mockMvc.perform(post("/api/v1/comments")
                        .content(content)
                        .header("Authorization", "Bearer {ACCESS_TOKEN}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("create-comments",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("boardId").description("Board 아이디"),
                                fieldWithPath("postId").description("Post 아이디"),
                                fieldWithPath("writerName").description("작성자명"),
                                fieldWithPath("content").description("Comments 내용"),
                                fieldWithPath("parentCommentId").description("부모 댓글 아이디"),
                                fieldWithPath("imageListDto").description("댓글 이미지 목록")
                        ),
                        responseFields(
                                fieldWithPath("message").description("응답 메세지"),
                                subsectionWithPath("result").description("결과 데이터"),
                                fieldWithPath("result.id").description("Comment 아이디"),
                                fieldWithPath("result.boardId").description("Board 아이디"),
                                fieldWithPath("result.postId").description("Post 아이디"),
                                fieldWithPath("result.userId").description("작성자 아이디"),
                                fieldWithPath("result.writerName").description("작성자명"),
                                fieldWithPath("result.content").description("Comments 내용"),
                                fieldWithPath("result.parentCommentId").description("부모 댓글 아이디"),
                                fieldWithPath("result.modifiedAt").description("수정일자"),
                                fieldWithPath("result.isDeleted").description("삭제 댓글")
                        )
                ));
    }

    @Test
    void deleteComment() throws Exception {
    }

    @Test
    void getCommentsByBoardId() throws Exception {
    }
}