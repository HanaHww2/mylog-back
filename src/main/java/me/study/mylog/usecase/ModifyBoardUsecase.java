package me.study.mylog.usecase;

import lombok.RequiredArgsConstructor;
import me.study.mylog.board.dto.BoardDetailResponse;
import me.study.mylog.board.dto.ModifyBoardRequest;
import me.study.mylog.board.entity.BoardMemberType;
import me.study.mylog.board.service.BoardMemberReadService;
import me.study.mylog.board.service.BoardWriteService;
import me.study.mylog.common.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ModifyBoardUsecase {

    private final BoardWriteService boardWriteService;
    private final BoardMemberReadService boardMemberReadService;

    // 게시글의 버저닝을 이용해서, 동시 수정 문제를 방지할 필요가 있음
    @Transactional
    public BoardDetailResponse execute(Long userId, ModifyBoardRequest modifyBoardRequest) {

        var boardMember = boardMemberReadService.getBoardMemberByBoardIdAndUserId(modifyBoardRequest.boardId(), userId);
        if (!Objects.equals(boardMember.getBoardMemberType(), BoardMemberType.MANAGER)) {
            throw new BadRequestException("Doesn't have authority");
        }

        return boardWriteService.modifyBoard(modifyBoardRequest);
    }

}
