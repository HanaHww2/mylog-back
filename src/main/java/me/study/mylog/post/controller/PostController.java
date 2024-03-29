package me.study.mylog.post.controller;

import lombok.RequiredArgsConstructor;
import me.study.mylog.auth.security.UserPrincipal;
import me.study.mylog.common.dto.ApiResponse;
import me.study.mylog.common.dto.PageResponse;
import me.study.mylog.post.dto.ModifyPostRequest;
import me.study.mylog.post.service.PostReadService;
import me.study.mylog.post.dto.PostDetailResponse;
import me.study.mylog.post.dto.PostMainResponse;
import me.study.mylog.post.dto.CreatePostRequest;
import me.study.mylog.usecase.CreatePostUsecase;
import me.study.mylog.usecase.DeletePostUsecase;
import me.study.mylog.usecase.ModifyPostUsecase;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

//@CrossOrigin /* 임시방편, 시큐리티 등을 적용하면 시큐리티 필터링 단계에서 다시 설정을 잡아줘야 한다. */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class PostController {

    private final PostReadService postReadService;
    private final CreatePostUsecase createPostUsecase;
    private final ModifyPostUsecase modifyPostUsecase;
    private final DeletePostUsecase deletePostUsecase;

    /**
     * 신규 게시글 저장
     * @param userPrincipal
     * @param createPostRequest
     * @return ResponseEntity<ApiResponse<PostDetailResponseDto>>
     */
    @PostMapping("/posts")
    public ResponseEntity<ApiResponse<PostDetailResponse>> savePost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CreatePostRequest createPostRequest) {

        PostDetailResponse responseDto = createPostUsecase.execute(createPostRequest, userPrincipal.getId());

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/id")
                .build()
                .toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse<>("success", responseDto));
    }

    /**
     * 게시글 아이디를 이용해 게시글 수정
     * @param userPrincipal
     * @param modifyPostRequest
     * @return
     */
    @PutMapping("/posts")
    public ResponseEntity<ApiResponse<PostDetailResponse>> modifyPost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody ModifyPostRequest modifyPostRequest) {

        PostDetailResponse postDetailResponse = modifyPostUsecase.execute(modifyPostRequest, userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse<>("success", postDetailResponse));
    }

    /**
     * 게시글 아이디를 이용해 게시글 삭제
     * @param userPrincipal
     * @param boardId
     * @param postId
     * @return
     */
    @DeleteMapping("/posts/{boardId}/{postId}")
    public ResponseEntity<ApiResponse<Boolean>> modifyPostById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long boardId,
            @PathVariable Long postId) {

        deletePostUsecase.execute(boardId, postId, userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse<>("success", Boolean.TRUE));
    }

    /**
     * 게시판 id를 이용해 게시판의 전체 게시글 목록 조회, jpa 페이징 기능 활용
     */
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<PageResponse<PostMainResponse>>> getAllPostByBoardId(@RequestParam(value ="boardId", required = false) Long boardId,
                                                                                           @RequestParam(value ="categoryId", required = false) Long categoryId,
                                                                                           @PageableDefault(sort="modifiedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        PageResponse<PostMainResponse> dtoList;
        if (categoryId != null) {
            dtoList =  postReadService.getAllPostDescByCategoryId(categoryId, pageable);
            return ResponseEntity.ok(new ApiResponse<>("success", dtoList));
        }
        dtoList= postReadService.getAllPostDescByBoardId(boardId, pageable);
        return ResponseEntity.ok(new ApiResponse<>("success", dtoList));
    }

    /**
     * 전체 게시글 목록 조회, jpa 페이징 기능 활용
     */
    @GetMapping("/posts/all")
    public ResponseEntity<ApiResponse<PageResponse<PostMainResponse>>> getAllPosts(@PageableDefault(sort="modifiedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        PageResponse<PostMainResponse> dtoList = postReadService.getAllPostDesc(pageable);
        return ResponseEntity.ok(new ApiResponse<>("success", dtoList));
    }

    /**
     * 게시글 아이디를 이용해 게시글을 조회하고 디테일 정보를 반환
     * @param postId
     * @return ResponseEntity<ApiResponse<PostDetailResponseDto>>
     */
    @GetMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPostDetailInfoById(@PathVariable("postId") Long postId) {

        PostDetailResponse responseDto = postReadService.getPostDetailInfoById(postId);
        return ResponseEntity.ok(new ApiResponse<>("success", responseDto));
    }

    // TODO 조회수 카운팅은 api가 필요 없을 것이라 생각됨.
    // 게시글 조횟수 카운팅 기능 메소드 등 구현 필요
    /*
     * 게시글 아이디를 이용해 게시글 조회수 업데이트 (일단은 패치 사용하지 않음)
     * @param postId
     * @return
     */
//    @PatchMapping ("/posts/{postId}/view")
//    public ResponseEntity<ApiResponse<Long>> updatePostViewsById(@PathVariable("postId") Long postId) {
//
//        Long views = postService.updatePostViewsById(postId);
//        return ResponseEntity.ok(new ApiResponse<>("success", views));
//    }
}