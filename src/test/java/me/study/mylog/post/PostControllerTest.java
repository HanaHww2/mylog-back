package me.study.mylog.post;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import me.study.mylog.auth.security.JwtAuthenticationFilter;
import me.study.mylog.common.domain.BaseTimeEntity;
import me.study.mylog.post.domain.Post;
import me.study.mylog.users.controller.UserController;
import me.study.mylog.users.dto.UserDto;
import me.study.mylog.util.PostFixtureFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Slf4j
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = PostController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
})
class PostControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    @MockBean
    PostService postService;

    List<Post> posts;

    @BeforeEach
    void setUp() {
        log.info("---------------- setUP ?????? ------------------");
//        objectMapper =  JsonMapper.builder().disable(MapperFeature.USE_ANNOTATIONS).build();
//        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);


        var easyRandom = PostFixtureFactory.get(1L);
        this.posts = IntStream.range(0, 10)
                .mapToObj(i -> easyRandom.nextObject(Post.class))
                .sorted(Comparator.comparing(BaseTimeEntity::getModifiedAt))
                .collect(Collectors.toList());
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("postList??? ??? ??????????????? ??????")
    public void chkGetPostListWell() throws Exception {
        // given
        List<PostMainResponseDto> dtoList = this.posts.stream()
                .map(PostMainResponseDto::new)
                .collect(Collectors.toList());

        String content = objectMapper.writeValueAsString(dtoList);

        //given(userService.register(any())).willReturn(response); ?????? ???????????? ?????? ??????
        BDDMockito.willReturn(dtoList).given(postService).getAllPostDesc(any());

        // when, then
        mockMvc.perform(get("/api/v1/posts/all?page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(10))
                //.andExpect(jsonPath("$.data[0].content", containsString("content")))
                //.andExpect(jsonPath("$.data[0].title", containsString("title")))
                .andDo(print())
                .andDo(document("all-post-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                List.of(
                                        parameterWithName("page").description("????????? ??????").optional(),
                                        parameterWithName("size").description("????????? ?????????").optional()
                                )),
                        responseFields(
                                fieldWithPath("message").description("?????? ?????????"),
                                subsectionWithPath("data[]").description("data"),
                                fieldWithPath("data[].id").description("Post ?????????").optional(),
                                fieldWithPath("data[].boardId").description("Post ????????? ?????????").optional(),
                                fieldWithPath("data[].boardName").description("Post ????????????").optional(),
                                fieldWithPath("data[].boardUri").description("Post ????????? URI").optional(),
                                fieldWithPath("data[].categoryId").description("Post ???????????? ?????????").optional(),
                                fieldWithPath("data[].categoryName").description("Post ???????????????").optional(),
                                fieldWithPath("data[].email").description("Post ????????? ?????????").optional(),
                                fieldWithPath("data[].authorName").description("Post ????????????").optional(),
                                fieldWithPath("data[].title").description("Post ??????").optional(),
                                fieldWithPath("data[].content").description("Post ??????").optional(),
                                fieldWithPath("data[].hashtagList").description("Post ???????????? ?????????").optional(),
                                fieldWithPath("data[].modifiedDate").description("Post ????????????").optional()
                        )
                ));
    }

//    @Test
//    @DisplayName("post??? ??? ??????????????? ??????")
//    public void checkSavePostWell() throws Exception {
//        // given
//        UserDto dto = UserDto.builder()
//                .email("test@example.com")
//                .name("hanah")
//                .nickname("hanah")
//                .password("1234")
//                .build();
//
//        String content = objectMapper.writeValueAsString(dto);
//        String location = "http://localhost:8080/users/me"; // ?????? ????????? ????????? ??????.
//
//        //given(userService.register(any())).willReturn(response); ?????? ???????????? ?????? ??????
//        BDDMockito.willReturn(dto).given(postService).save(any(), any());
//
//        // when, then
//        mockMvc.perform(post("/api/v1/posts")
//                        .with(SecurityMockMvcRequestPostProcessors.csrf())
//                        .content(content)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print());
////                .andDo(document("user-register",
////                        requestFields(
////                                fieldWithPath("email").description("User ?????????"),
////                                fieldWithPath("name").description("User ????????? ??????").optional(),
////                                fieldWithPath("nickname").description("User ??????").optional(),
////                                fieldWithPath("password").description("User ????????????")
////                        )
////                ));
//    }
}