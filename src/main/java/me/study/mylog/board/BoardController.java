package me.study.mylog.board;

import lombok.AllArgsConstructor;
import me.study.mylog.auth.security.UserPrincipal;
import me.study.mylog.category.CategoryResponseDto;
import me.study.mylog.category.CategoryService;
import me.study.mylog.common.dto.CommonResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
        List<BoardResponseDto> dto = boardService.getBoardsByUserEmail(principal.getName());
        return new ResponseEntity<>(new CommonResult<>("get BoardsList Successfully", dto), HttpStatus.OK);
    }
}
