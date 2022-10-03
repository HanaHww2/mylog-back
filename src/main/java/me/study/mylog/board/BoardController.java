package me.study.mylog.board;

import lombok.AllArgsConstructor;
import me.study.mylog.common.dto.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class BoardController {

    BoardService boardService;

    @GetMapping("/v1/boards")
    public ResponseEntity<?> getBoardsByUser(Principal principal) {
        List<BoardDetailResponseDto> dtoList = boardService.getBoardsByUserEmail(principal.getName());
        return new ResponseEntity<>(new CommonResponse<>("get BoardsList Successfully", dtoList), HttpStatus.OK);
    }

    @GetMapping("/v1/boards/{boardId}")
    public ResponseEntity<?> getBoardById(@PathVariable("boardId") Long boardId, Principal principal) {
        BoardDetailResponseDto dto = boardService.getBoardById(boardId);
        return new ResponseEntity<>(new CommonResponse<>("get BoardsList Successfully", dto), HttpStatus.OK);
    }
}
