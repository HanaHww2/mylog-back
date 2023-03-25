package me.study.mylog.usecase;

import lombok.RequiredArgsConstructor;
import me.study.mylog.board.entity.BoardMemberType;
import me.study.mylog.board.service.BoardMemberReadService;
import me.study.mylog.common.exception.BadRequestException;
import me.study.mylog.post.service.PostReadService;
import me.study.mylog.post.service.PostWriteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class DeletePostUsecase {

    private final BoardMemberReadService boardMemberReadService;
    private final PostReadService postReadService;
    private final PostWriteService postWriteService;

    @Transactional
    public void execute(Long boardId, Long postId, Long userId) {
        var post = postReadService.findPostById(postId);

        if (!Objects.equals(userId, post.getUserId())) {
            // 글 작성자가 아니더라도, 게시판 매니저라면 삭제 가능하다.
            var boardMember = boardMemberReadService.getBoardMemberByBoardIdAndUserId(boardId, userId);
            if (!Objects.equals(boardMember.getBoardMemberType(), BoardMemberType.MANAGER)) {
                throw new BadRequestException("Doesn't have authority");
            }
            throw new BadRequestException("Doesn't have authority");
        }
        //post.softDelete();
        postWriteService.delete(post);
    }
}
