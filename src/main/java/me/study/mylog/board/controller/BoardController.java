package me.study.mylog.board.controller;

import lombok.AllArgsConstructor;
import me.study.mylog.auth.security.UserPrincipal;
import me.study.mylog.board.dto.BoardDetailResponse;
import me.study.mylog.board.dto.CreateBoardRequest;
import me.study.mylog.board.dto.ModifyBoardRequest;
import me.study.mylog.board.service.BoardMemberReadService;
import me.study.mylog.board.service.BoardReadService;
import me.study.mylog.common.dto.ApiResponse;
import me.study.mylog.usecase.CreateBoardUsecase;
import me.study.mylog.usecase.DeleteBoardUsecase;
import me.study.mylog.usecase.ModifyBoardUsecase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class BoardController {

    private final CreateBoardUsecase createBoardUsecase;
    private final ModifyBoardUsecase modifyBoardUsecase;
    private final DeleteBoardUsecase deleteBoardUsecase;

    private final BoardReadService boardReadService;
    private final BoardMemberReadService boardMemberReadService;

    @PostMapping("/boards")
    public ResponseEntity<?> createNewBoard(@AuthenticationPrincipal UserPrincipal userPrincipal, CreateBoardRequest createBoardRequest) {

        var dto = createBoardUsecase.execute(userPrincipal.getId(), createBoardRequest);
        return new ResponseEntity<>(new ApiResponse<>("SUCCESS", dto), HttpStatus.OK);
    }

    @PutMapping("/boards")
    public ResponseEntity<?> modifyBoard(@AuthenticationPrincipal UserPrincipal userPrincipal, ModifyBoardRequest modifyBoardRequest) {

        BoardDetailResponse dto = modifyBoardUsecase.execute(userPrincipal.getId(), modifyBoardRequest);
        return new ResponseEntity<>(new ApiResponse<>("SUCCESS", dto), HttpStatus.OK);
    }

    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<?> deleteBoard(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long boardId) {

        deleteBoardUsecase.execute(userPrincipal.getId(), boardId);
        return new ResponseEntity<>(new ApiResponse<>("SUCCESS", Boolean.TRUE), HttpStatus.OK);
    }

    @GetMapping("/boards")
    public ResponseEntity<ApiResponse<List<BoardDetailResponse>>> getBoardsByUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {

        List<BoardDetailResponse> dtoList = boardMemberReadService.getBoardsByUserId(userPrincipal.getId());
        return new ResponseEntity<>(new ApiResponse<>("get BoardsList Successfully", dtoList), HttpStatus.OK);
    }

    @GetMapping("/boards/{boardId}")
    public ResponseEntity<ApiResponse<BoardDetailResponse>> getBoardById(@PathVariable("boardId") Long boardId) {

        BoardDetailResponse dto = boardReadService.getBoardById(boardId);
        return new ResponseEntity<>(new ApiResponse<>("get Board Detail Successfully", dto), HttpStatus.OK);
    }

    @GetMapping("/boards/@{boardName}")
    public ResponseEntity<ApiResponse<BoardDetailResponse>> getBoardsByUri(@PathVariable String boardName) {

        BoardDetailResponse dto = boardReadService.getBoardByUri(boardName);
        return new ResponseEntity<>(new ApiResponse<>("get Board Detail Successfully", dto), HttpStatus.OK);
    }

    // TODO 게시판 방문수
}
