package me.study.mylog.usecase;

import lombok.RequiredArgsConstructor;
import me.study.mylog.board.dto.BoardDetailResponse;
import me.study.mylog.board.dto.ModifyBoardRequest;
import me.study.mylog.board.entity.BoardMember;
import me.study.mylog.board.entity.BoardMemberType;
import me.study.mylog.board.service.BoardMemberReadService;
import me.study.mylog.board.service.BoardWriteService;
import me.study.mylog.common.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class DeleteBoardUsecase {
    private final BoardWriteService boardWriteService;
    private final BoardMemberReadService boardMemberReadService;

    @Transactional
    public void execute(Long userId, Long boardId) {

        var boardMember = boardMemberReadService.getBoardMemberByBoardIdAndUserId(boardId, userId);
        if (!Objects.equals(boardMember.getBoardMemberType(), BoardMemberType.MANAGER)) {
            throw new BadRequestException("Doesn't have authority");
        }
        boardWriteService.deleteBoard(boardId);
    }
}
