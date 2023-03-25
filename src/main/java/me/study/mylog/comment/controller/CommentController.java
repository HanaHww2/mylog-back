package me.study.mylog.comment.controller;

import lombok.RequiredArgsConstructor;
import me.study.mylog.auth.security.UserPrincipal;
import me.study.mylog.comment.dto.CommentResponse;
import me.study.mylog.comment.dto.ModifyCommentRequest;
import me.study.mylog.comment.dto.CreateCommentRequest;
import me.study.mylog.comment.service.CommentReadService;
import me.study.mylog.comment.service.CommentWriteService;
import me.study.mylog.common.dto.ApiResponse;
import me.study.mylog.common.dto.PageResponse;
import me.study.mylog.usecase.DeleteCommentUsecase;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class CommentController {

    private final CommentReadService commentReadService;
    private final CommentWriteService commentWriteService;
    private final DeleteCommentUsecase deleteCommentUsecase;

    /**
     * 신규 댓글 저장
     * @param userPrincipal
     * @param createCommentRequest
     * @return ResponseEntity<ApiResponse<CommentDetailResponse>>
     */
    @PostMapping("/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CreateCommentRequest createCommentRequest) {

        CommentResponse response = commentWriteService.createNewComment(createCommentRequest, userPrincipal.getId());

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/id")
                .build()
                .toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse<>("success", response));
    }

    /**
     * 댓글 수정
     * @param userPrincipal
     * @param modifyCommentRequest
     * @return ResponseEntity<ApiResponse<Boolean>>
     */
    @PutMapping("/comments")
    public ResponseEntity<ApiResponse<Boolean>> modifyComment(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody ModifyCommentRequest modifyCommentRequest) {
        commentWriteService.modifyComment(modifyCommentRequest, userPrincipal.getId());

        return ResponseEntity.ok(new ApiResponse<>("Successfully Updated", Boolean.TRUE));
    }

    /**
     * 게시판/글/댓글 아이디를 이용해 댓글 삭제
     * @param userPrincipal
     * @param boardId
     * @param commentId
     * @return
     */
    @DeleteMapping("/comments/{boardId}/{commentId}")
    public ResponseEntity<ApiResponse<Boolean>> deleteComment(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long boardId,
            @PathVariable Long commentId) {

        deleteCommentUsecase.execute(boardId, commentId, userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse<>("success", Boolean.TRUE));
    }

    /**
     * 게시글 id를 이용해 게시글의 전체 댓글 목록 조회
     * jpa 페이징 기능 활용
     */
    @GetMapping("/comments")
    public ResponseEntity<ApiResponse<PageResponse<CommentResponse>>> getCommentsByBoardId(@RequestParam(value ="postId") Long postId,
                                                                                           @PageableDefault(sort="modifiedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        var dtoList= commentReadService.getAllByPostIdDesc(postId, pageable);
        return ResponseEntity.ok(new ApiResponse<>("success", dtoList));
    }
}