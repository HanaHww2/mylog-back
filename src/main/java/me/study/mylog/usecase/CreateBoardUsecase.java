package me.study.mylog.usecase;

import lombok.RequiredArgsConstructor;
import me.study.mylog.board.dto.BoardDetailResponse;
import me.study.mylog.board.dto.CreateBoardRequest;
import me.study.mylog.board.mapper.BoardMapper;
import me.study.mylog.board.service.BoardMemberWriteService;
import me.study.mylog.board.service.BoardWriteService;
import me.study.mylog.users.service.UserReadService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CreateBoardUsecase {
    private final UserReadService userReadService;
    private final BoardWriteService boardWriteService;
    private final BoardMemberWriteService boardMemberWriteService;

    public BoardDetailResponse execute(Long userId, CreateBoardRequest createBoardRequest) {

        // 새로운 보드를 생성하고, 생성한 사용자는 MANAGER로 보드 멤버에 등록되는 로직 수행
        var user = userReadService.findUserById(userId);
        var board = BoardMapper.toEntity(createBoardRequest);
        BoardDetailResponse boardDto = boardWriteService.createNewBoard(createBoardRequest);
        boardMemberWriteService.addFirstBoardMember(board, user, createBoardRequest.nickname());
        return boardDto;
    }

}
