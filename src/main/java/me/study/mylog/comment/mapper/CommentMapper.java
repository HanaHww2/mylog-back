package me.study.mylog.comment.mapper;

import me.study.mylog.comment.dto.CommentResponse;
import me.study.mylog.comment.dto.CreateCommentRequest;
import me.study.mylog.comment.entity.Comment;

public class CommentMapper {

    public static Comment toEntity(CreateCommentRequest request, Long userId) {
        return Comment.builder()
                .boardId(request.getBoardId())
                .postId(request.getPostId())
                .parentCommentId(request.getParentCommentId())
                .userId(userId)
                .writerName(request.getWriterName())
                .content(request.getContent())
                .build();
    }

    public static CommentResponse toResponseDto(Comment comment) {
        return CommentResponse.builder()
                .boardId(comment.getBoardId())
                .postId(comment.getPostId())
                .parentCommentId(comment.getParentCommentId())
                .content(comment.getContent())
                .writerName(comment.getWriterName())
                .modifiedAt(comment.getModifiedAt())
                .build();
    }

}
