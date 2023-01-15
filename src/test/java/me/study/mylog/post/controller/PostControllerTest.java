package me.study.mylog.post.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.study.mylog.auth.security.JwtAuthenticationFilter;
import me.study.mylog.auth.security.UserPrincipal;
import me.study.mylog.common.domain.BaseTimeEntity;
import me.study.mylog.post.dto.ModifyPostRequest;
import me.study.mylog.post.service.PostReadService;
import me.study.mylog.post.entity.Post;
import me.study.mylog.post.dto.PostDetailResponse;
import me.study.mylog.post.dto.PostMainResponse;
import me.study.mylog.post.service.PostWriteService;
import me.study.mylog.usecase.CreatePostUsecase;
import me.study.mylog.usecase.ModifyPostUsecase;
import me.study.mylog.users.domain.RoleType;
import me.study.mylog.util.fixture.PostFixtureFactory;
import me.study.mylog.util.security.WithMockCustomUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Slf4j
@AutoConfigureRestDocs
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = PostController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
})
class PostControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private CreatePostUsecase createPostUsecase;
    @MockBean
    private ModifyPostUsecase modifyPostUsecase;
    @MockBean
    private PostReadService postReadService;
    @MockBean
    private PostWriteService postWriteService;

    private List<Post> posts;

    @BeforeEach
    void setUp() {
        log.info("---------------- setUP 실행 ------------------");
//        objectMapper = JsonMapper.builder().disable(MapperFeature.USE_ANNOTATIONS).build();
//        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        var easyRandom = PostFixtureFactory.getWithId(1L);
        this.posts = IntStream.range(0, 10)
                .mapToObj(i -> easyRandom.nextObject(Post.class))
                .sorted(Comparator.comparing(BaseTimeEntity::getModifiedAt))
                .collect(Collectors.toList());
    }

    @AfterEach
    void tearDown() {
    }

    @DisplayName("postList를 잘 반환하는지 검증")
    @Test
    public void chkGetPostListWell() throws Exception {
        // given
        List<PostMainResponse> dtoList = this.posts.stream()
                .map(PostMainResponse::new)
                .collect(Collectors.toList());
        var postMainResponseDtoPage = new PageImpl<>(dtoList);

        String content = objectMapper.writeValueAsString(dtoList);

        //given(userService.register(any())).willReturn(response); 중첩 구조여서 에러 발생
        BDDMockito.willReturn(postMainResponseDtoPage).given(postReadService).getAllPostDesc(any());

        // when, then
        mockMvc.perform(get("/api/v1/posts/all?page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result.content").isArray())
                .andExpect(jsonPath("$.result.content.length()").value(10))
//                .andExpect(jsonPath("$.result.content[0].content", containsString("content")))
//                .andExpect(jsonPath("$.result.content[0].title", containsString("title")))
                .andDo(print())
                .andDo(document("all-post-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                List.of(
                                        parameterWithName("page").description("페이지 번호").optional(),
                                        parameterWithName("size").description("페이지 사이즈").optional()
                                )),
                        responseFields(
                                fieldWithPath("message").description("응답 메세지"),
                                subsectionWithPath("result").description("결과 데이터"),
                                fieldWithPath("result.content").description("content"),
                                fieldWithPath("result.content[].id").description("Post 아이디").optional(),
                                fieldWithPath("result.content[].boardId").description("Post 게시판 아이디").optional(),
                                fieldWithPath("result.content[].boardName").description("Post 게시판명").optional(),
                                fieldWithPath("result.content[].boardUri").description("Post 게시판 URI").optional(),
                                fieldWithPath("result.content[].categoryId").description("Post 카테고리 아이디").optional(),
                                fieldWithPath("result.content[].categoryName").description("Post 카테고리명").optional(),
                                fieldWithPath("result.content[].email").description("Post 작성자 이메일").optional(),
                                fieldWithPath("result.content[].authorName").description("Post 작성자명").optional(),
                                fieldWithPath("result.content[].title").description("Post 제목").optional(),
                                fieldWithPath("result.content[].content").description("Post 내용").optional(),
                                fieldWithPath("result.content[].hashtagList").description("Post 해시태그 리스트").optional(),
                                fieldWithPath("result.content[].modifiedAt").description("Post 수정일자").optional()
                        )
                ));
    }

    @DisplayName("post 상세 데이터를 postId로 잘 조회해오는지 검증")
    @Test
    public void searchPostByIdWell() throws Exception {
        // given
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(1L)
                .build();

        var authorities = Collections.
                singletonList(new SimpleGrantedAuthority(RoleType.USER.getCode()));
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(userPrincipal, "", authorities));

        PostDetailResponse dto = PostDetailResponse.builder()
                .categoryId(1L)
                .boardId(1L)
                .title("제목")
                .content("내용입니다.")
                .hashtagList(Set.of(new String[]{"가", "나", "다"}))
                //.imageListDto()
                .build();

        //given(userService.register(any())).willReturn(response); 중첩 구조여서 에러 발생
        BDDMockito.willReturn(dto).given(postReadService).getPostDetailInfoById(any());

        // when, then
        mockMvc.perform(get("/api/v1/posts/{postId}", 14L))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-post-by-postId",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("postId").description("게시글 아이디")
                        ),
                        responseFields(
                                fieldWithPath("message").description("응답 메세지"),
                                subsectionWithPath("result").description("결과 데이터"),
                                fieldWithPath("result.id").description("Post 아이디"),
                                fieldWithPath("result.boardId").description("Post 게시판 아이디"),
                                fieldWithPath("result.boardName").description("Post 게시판명"),
                                fieldWithPath("result.boardUri").description("Post 게시판 URI"),
                                fieldWithPath("result.categoryId").description("Post 카테고리 아이디"),
                                fieldWithPath("result.categoryName").description("Post 카테고리명"),
                                fieldWithPath("result.email").description("Post 작성자 이메일"),
                                fieldWithPath("result.authorName").description("Post 작성자명"),
                                fieldWithPath("result.title").description("Post 제목"),
                                fieldWithPath("result.content").description("Post 내용"),
                                fieldWithPath("result.hashtagList").description("Post 해시태그 리스트"),
                                fieldWithPath("result.views").description("Post 조회수"),
                                fieldWithPath("result.modifiedAt").description("Post 수정일자")
                        )
                ));
    }

    @DisplayName("post를 잘 등록하는지 검증")
    @Test
    public void checkSavePostWell() throws Exception {
        // given
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(1L)
                .build();

       var authorities = Collections.
               singletonList(new SimpleGrantedAuthority(RoleType.USER.getCode()));
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(userPrincipal, "", authorities));
        PostDetailResponse dto = PostDetailResponse.builder()
                .id(11L)
                .categoryId(1L)
                .boardId(1L)
                .title("제목")
                .content("내용입니다.")
                .hashtagList(Set.of(new String[]{"가", "나", "다"}))
                //.imageListDto()
                .build();

        String content = objectMapper.writeValueAsString(dto);
        String location = "http://localhost:8080/users/me"; // 포트 정보가 보이지 않음.

        //given(userService.register(any())).willReturn(response); 중첩 구조여서 에러 발생
        BDDMockito.willReturn(dto).given(createPostUsecase).execute(any(), any());

        // when, then
        mockMvc.perform(post("/api/v1/posts")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer {ACCESS_TOKEN}"))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("create-post",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("id").description("Post 아이디"),
                                fieldWithPath("boardId").description("Post 게시판 아이디"),
                                fieldWithPath("boardName").description("Post 게시판명"),
                                fieldWithPath("boardUri").description("Post 게시판 URI"),
                                fieldWithPath("categoryId").description("Post 카테고리 아이디"),
                                fieldWithPath("categoryName").description("Post 카테고리명"),
                                fieldWithPath("email").description("Post 작성자 이메일"),
                                fieldWithPath("authorName").description("Post 작성자명"),
                                fieldWithPath("title").description("Post 제목"),
                                fieldWithPath("content").description("Post 내용"),
                                fieldWithPath("hashtagList").description("Post 해시태그 리스트"),
                                fieldWithPath("views").description("Post 조회수"),
                                fieldWithPath("modifiedAt").description("Post 수정일자")
                        ),
                        responseFields(
                                fieldWithPath("message").description("응답 메세지"),
                                subsectionWithPath("result").description("결과 데이터"),
                                fieldWithPath("result.id").description("Post 아이디"),
                                fieldWithPath("result.boardId").description("Post 게시판 아이디"),
                                fieldWithPath("result.boardName").description("Post 게시판명"),
                                fieldWithPath("result.boardUri").description("Post 게시판 URI"),
                                fieldWithPath("result.categoryId").description("Post 카테고리 아이디"),
                                fieldWithPath("result.categoryName").description("Post 카테고리명"),
                                fieldWithPath("result.email").description("Post 작성자 이메일"),
                                fieldWithPath("result.authorName").description("Post 작성자명"),
                                fieldWithPath("result.title").description("Post 제목"),
                                fieldWithPath("result.content").description("Post 내용"),
                                fieldWithPath("result.hashtagList").description("Post 해시태그 리스트"),
                                fieldWithPath("result.views").description("Post 조회수"),
                                fieldWithPath("result.modifiedAt").description("Post 수정일자")
                        )
                ));
    }

    @WithMockCustomUser
    @DisplayName("post를 잘 수정하는지 검증")
    @Test
    public void checkIfModifyPostWell() throws Exception {
        // given
        ModifyPostRequest modifyPostRequest = ModifyPostRequest.builder()
                .postId(13L)
                .title("post 수정하기")
                .content("test 데이터입니다.")
                .categoryId(3L)
                .tagList(Set.of(new String[] {"tag1", "tag2"}))
                .build();

        PostDetailResponse response = PostDetailResponse.builder()
                .categoryId(1L)
                .boardId(1L)
                .title("제목")
                .content("내용입니다.")
                .hashtagList(Set.of(new String[]{"가", "나", "다"}))
                //.imageListDto()
                .build();

        String content = objectMapper.writeValueAsString(modifyPostRequest);
        BDDMockito.willReturn(response).given(modifyPostUsecase).execute(any(), any());

        // when, then
        mockMvc.perform(put("/api/v1/posts")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer {ACCESS_TOKEN}"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("modify-post",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("postId").description("Post 아이디"),
                                fieldWithPath("boardId").description("Post 게시판 아이디"),
                                fieldWithPath("categoryId").description("Post 카테고리 아이디"),
                                fieldWithPath("title").description("Post 제목"),
                                fieldWithPath("content").description("Post 내용"),
                                fieldWithPath("tagList").description("Post 해시태그 리스트")
                        ),
                        responseFields(
                                fieldWithPath("message").description("응답 메세지"),
                                subsectionWithPath("result").description("결과 데이터"),
                                fieldWithPath("result.id").description("Post 아이디"),
                                fieldWithPath("result.boardId").description("Post 게시판 아이디"),
                                fieldWithPath("result.boardName").description("Post 게시판명"),
                                fieldWithPath("result.boardUri").description("Post 게시판 URI"),
                                fieldWithPath("result.categoryId").description("Post 카테고리 아이디"),
                                fieldWithPath("result.categoryName").description("Post 카테고리명"),
                                fieldWithPath("result.email").description("Post 작성자 이메일"),
                                fieldWithPath("result.authorName").description("Post 작성자명"),
                                fieldWithPath("result.title").description("Post 제목"),
                                fieldWithPath("result.content").description("Post 내용"),
                                fieldWithPath("result.hashtagList").description("Post 해시태그 리스트"),
                                fieldWithPath("result.views").description("Post 조회수"),
                                fieldWithPath("result.modifiedAt").description("Post 수정일자")
                        )
                ));
    }
}