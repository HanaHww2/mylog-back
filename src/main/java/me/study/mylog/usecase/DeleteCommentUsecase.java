package me.study.mylog.usecase;

import lombok.RequiredArgsConstructor;
import me.study.mylog.board.entity.BoardMemberType;
import me.study.mylog.board.service.BoardMemberReadService;
import me.study.mylog.comment.service.CommentReadService;
import me.study.mylog.comment.service.CommentWriteService;
import me.study.mylog.common.exception.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class DeleteCommentUsecase {

    private final BoardMemberReadService boardMemberReadService;
    private final CommentWriteService commentWriteService;
    private final CommentReadService commentReadService;

    public void execute(Long boardId, Long commentId, Long userId) {

        var comment = commentReadService.findById(commentId);
        if (Objects.equals(userId, comment.getUserId())) {
            commentWriteService.delete(comment);
            return;
        }

        var boardMember = boardMemberReadService.getBoardMemberByBoardIdAndUserId(boardId, userId);
        if (!Objects.equals(boardMember.getBoardMemberType(), BoardMemberType.MANAGER)) {
            throw new BadRequestException("Doesn't have authority");
        }
        commentWriteService.delete(comment);
    }
}
