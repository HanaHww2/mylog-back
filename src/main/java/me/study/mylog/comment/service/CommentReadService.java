package me.study.mylog.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.study.mylog.comment.dto.CommentDetailResponse;
import me.study.mylog.comment.dto.CommentResponse;
import me.study.mylog.comment.entity.Comment;
import me.study.mylog.comment.mapper.CommentMapper;
import me.study.mylog.comment.repository.CommentRepository;
import me.study.mylog.common.dto.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentReadService {

    private final CommentRepository commentRepository;

    /**
     * 게시글 내 전체 댓글 조회
     */
    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> getAllByPostIdDesc(Long postId, Pageable pageable) {
        Page<CommentResponse> dtoList = commentRepository.findAllByPostId(postId, pageable)
                .map(CommentMapper::toResponseDto);
        return new PageResponse(dtoList);
    }

    /**
     * 특정 게시판 내 최신 댓글 조회
     */
    /* readOnly 사용시, 트랜잭션 범위는 유지하되, 조회 기능만 남겨두어 조회 속도 개선 */
    @Transactional(readOnly=true)
    public PageResponse<CommentResponse> getRecentCommentsByBoardIdDesc(Long boardId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findAllByBoardId(boardId, pageable);
        Page<CommentResponse> dtoList = comments.map(CommentMapper::toResponseDto);

        return new PageResponse(dtoList);
    }

    /**
     * 게시글 단 건 상세 조회, usecase 용
     */
    @Transactional(readOnly = true)
    public Comment findById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 글이 존재하지 않습니다."));
    }

    /**
     * 게시글 단 건 상세 조회
     */
    @Transactional(readOnly = true)
    public CommentDetailResponse getDetailInfoById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 글이 존재하지 않습니다."));
        return CommentDetailResponse.builder().build(); //TODO
    }
}
