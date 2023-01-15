package me.study.mylog.usecase;

import lombok.RequiredArgsConstructor;
import me.study.mylog.board.dto.BoardDetailResponse;
import me.study.mylog.board.dto.ModifyBoardRequest;
import me.study.mylog.board.service.BoardMemberReadService;
import me.study.mylog.board.service.BoardWriteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ModifyBoardUsecase {

    private final BoardWriteService boardWriteService;
    private final BoardMemberReadService boardMemberReadService;

    @Transactional
    public BoardDetailResponse execute(Long userId, ModifyBoardRequest modifyBoardRequest) {

        boardMemberReadService.existsBoardMemberByBoardIdAndUserId(modifyBoardRequest.boardId(), userId);
        return boardWriteService.modifyBoard(modifyBoardRequest);
    }
}
