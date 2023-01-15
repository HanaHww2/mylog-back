package me.study.mylog.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.study.mylog.auth.security.UserPrincipal;
import me.study.mylog.category.dto.CategoryCreateRequest;
import me.study.mylog.category.dto.CategoryModifyRequest;
import me.study.mylog.category.dto.CategoryResponseDto;
import me.study.mylog.category.service.CategoryReadService;
import me.study.mylog.category.service.CategoryWriteService;
import me.study.mylog.usecase.CreateCategoryUsecase;
import me.study.mylog.usecase.GetCategoryDetailUsecase;
import me.study.mylog.usecase.ModifyCategoryValueUsecase;
import me.study.mylog.users.domain.RoleType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@AutoConfigureRestDocs
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@SpringBootTest
class CategoryControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    @MockBean
    private CategoryReadService categoryReadService;
    @MockBean
    private CreateCategoryUsecase createCategoryUsecase;
    @MockBean
    private GetCategoryDetailUsecase getCategoryDetailUsecase;
    @MockBean
    private ModifyCategoryValueUsecase modifyCategoryValueUsecase;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @DisplayName("보드 id에 따라 카테고리들을 조회하는지 검증")
    @Test
    void getCategoriesByBoard() throws Exception {
        List<CategoryResponseDto> dtoList = LongStream.range(0, 10)
                .mapToObj(i -> CategoryResponseDto.builder()
                        .id(i)
                        .name("name" + i)
                        .parentId( i%2==0 ? i+1 : null )
                        .uri("/name/"+i)
                        .children(Collections.EMPTY_LIST)
                        .build())
                .collect(Collectors.toList());

        String content = objectMapper.writeValueAsString(dtoList);

        given(categoryReadService.getCategoriesByBoardId(any())).willReturn(dtoList);
        //BDDMockito.willReturn(content).given(categoryReadService).getCategoriesByBoardId(any());

        // when, then
        mockMvc.perform(get("/api/v1/categories?boardId=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("category-list-by-board",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(parameterWithName("boardId").description("보드 아이디")),
                        responseFields(
                                fieldWithPath("message").description("응답 메세지"),
                                subsectionWithPath("result").description("결과 데이터"),
                                fieldWithPath("result[].id").description("Category 아이디"),
                                fieldWithPath("result[].name").description("Category 이름"),
                                fieldWithPath("result[].parentId").description("Category 명").optional(),
                                fieldWithPath("result[].uri").description("Category URI").optional(),
                                fieldWithPath("result[].children").description("Category 자식 분류 리스트").optional()
                        )
                ));
    }

    @WithMockUser
    @DisplayName("카테고리 id에 따라 카테고리 상세 정보들을 조회하는지 검증")
    @Test
    void getCategoryDetailForAdminByCategoryId() throws Exception{

        // given
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(1L)
                .build();
        var authorities = Collections.
                singletonList(new SimpleGrantedAuthority(RoleType.USER.getCode()));
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(userPrincipal, "", authorities));

        CategoryResponseDto dto = CategoryResponseDto.builder()
                        .id(14L)
                        .name("name_test")
                        .parentId(4L)
                        .uri("/name/1")
                        .children(Collections.EMPTY_LIST)
                        .build();

        given(getCategoryDetailUsecase.execute(any(), any(), any())).willReturn(dto);

        // when, then
        mockMvc.perform(get("/api/v1/categories/{boardId}/{categoryId}", 1, 14))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("category-info-by-id",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("boardId").description("보드 아이디"),
                                parameterWithName("categoryId").description("카테고리 아이디")
                        ),
                        responseFields(
                                fieldWithPath("message").description("응답 메세지"),
                                subsectionWithPath("result").description("결과 데이터"),
                                fieldWithPath("result.id").description("Category 아이디"),
                                fieldWithPath("result.name").description("Category 이름"),
                                fieldWithPath("result.parentId").description("Category 명").optional(),
                                fieldWithPath("result.uri").description("Category URI").optional(),
                                fieldWithPath("result.children").description("Category 자식 분류 리스트").optional()
                        )
                ));
    }

    @WithMockUser
    @DisplayName("카테고리를 잘 생성하는지 검증")
    @Test
    void createNewCategory() throws Exception {

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(1L)
                .build();
        var authorities = Collections.
                singletonList(new SimpleGrantedAuthority(RoleType.USER.getCode()));
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(userPrincipal, "", authorities));

        CategoryCreateRequest requestDto = CategoryCreateRequest.builder()
                .name("name_test")
                .parentId(4L)
                .boardId(1L)
                .uri("/name/1")
                .build();

        CategoryResponseDto responseDto = CategoryResponseDto.builder()
                .id(14L)
                .name("name_test")
                .parentId(4L)
                .boardId(1L)
                .uri("/name/1")
                .children(Collections.EMPTY_LIST)
                .build();

        String content = objectMapper.writeValueAsString(requestDto);

        given(createCategoryUsecase.createNewCategory(any(), any())).willReturn(responseDto);

        // when, then
        mockMvc.perform(post("/api/v1/categories")
                        .content(content)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("create-category",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("categoryId").description("Category 아이디"),
                                fieldWithPath("name").description("Category 이름"),
                                fieldWithPath("uri").description("Category Uri"),
                                fieldWithPath("parentId").description("Category 부모 아이디"),
                                fieldWithPath("boardId").description("Category 가 등록된 게시판 아이디")
                                ),
                        responseFields(
                                fieldWithPath("message").description("응답 메세지"),
                                subsectionWithPath("result").description("결과 데이터"),
                                fieldWithPath("result.id").description("Category 아이디"),
                                fieldWithPath("result.name").description("Category 이름"),
                                fieldWithPath("result.uri").description("Category URI"),
                                fieldWithPath("result.parentId").description("Category 부모 아이디").optional(),
                                fieldWithPath("result.children").description("Category 자식 리스트").optional()
                        )
                ));
    }

    @WithMockUser
    @DisplayName("카테고리를 잘 수정하는지 검증")
    @Test
    void modifyCategory() throws Exception {

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(1L)
                .build();
        var authorities = Collections.
                singletonList(new SimpleGrantedAuthority(RoleType.USER.getCode()));
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(userPrincipal, "", authorities));

        CategoryModifyRequest requestDto = CategoryModifyRequest.builder()
                .categoryId(12L)
                .name("name_test12")
                .build();

        CategoryResponseDto responseDto = CategoryResponseDto.builder()
                .id(12L)
                .name("name_test12")
                .parentId(4L)
                .boardId(1L)
                .uri("/name/1")
                .children(Collections.EMPTY_LIST)
                .build();

        String content = objectMapper.writeValueAsString(requestDto);

        given(modifyCategoryValueUsecase.execute(any(), any())).willReturn(responseDto);

        // when, then
        mockMvc.perform(put("/api/v1/categories")
                        .content(content)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("modify-category-value",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("categoryId").description("Category 아이디"),
                                fieldWithPath("boardId").description("Board 아이디"),
                                fieldWithPath("name").description("Category 이름")
                        ),
                        responseFields(
                                fieldWithPath("message").description("응답 메세지"),
                                subsectionWithPath("result").description("결과 데이터"),
                                fieldWithPath("result.id").description("Category 아이디"),
                                fieldWithPath("result.name").description("Category 이름"),
                                fieldWithPath("result.uri").description("Category URI"),
                                fieldWithPath("result.parentId").description("Category 부모 아이디").optional(),
                                fieldWithPath("result.children").description("Category 자식 리스트").optional()
                        )
                ));
    }
}