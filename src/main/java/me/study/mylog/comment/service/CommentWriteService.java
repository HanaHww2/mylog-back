package me.study.mylog.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.study.mylog.comment.dto.CommentResponse;
import me.study.mylog.comment.dto.CreateCommentRequest;
import me.study.mylog.comment.dto.ModifyCommentRequest;
import me.study.mylog.comment.entity.Comment;
import me.study.mylog.comment.mapper.CommentMapper;
import me.study.mylog.comment.repository.CommentRepository;
import me.study.mylog.common.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentWriteService {

    private final CommentRepository commentRepository;

    public CommentResponse createNewComment(CreateCommentRequest createCommentRequest, Long userId) {

        var comment = CommentMapper.toEntity(createCommentRequest, userId);
        commentRepository.save(comment);
        return CommentMapper.toResponseDto(comment);
    }


    public void modifyComment(ModifyCommentRequest request, Long userId) {
        var comment = commentRepository.findByIdAndUserId(request.getCommentId(), userId)
                .orElseThrow(() -> { throw new BadRequestException("Not Found Comment To Update");});
        comment.modifyValue(request);
    }

    /**
     * 게시글 소프트 딜리트
     */
    @Transactional
    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }

}
