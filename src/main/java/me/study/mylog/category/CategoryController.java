package me.study.mylog.category;

import lombok.AllArgsConstructor;
import me.study.mylog.auth.security.UserPrincipal;
import me.study.mylog.common.dto.CommonResponse;
import me.study.mylog.users.domain.User;
import me.study.mylog.users.service.UserService;
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
@RequestMapping("/api/v1")
public class CategoryController {

    CategoryService categoryService;

    @GetMapping("/boards/{boardId}/categories")
    public ResponseEntity<?> getCategoriesByUser(Principal principal, @PathVariable Long boardId) {
        List<CategoryResponseDto> dto = categoryService.getCategoriesByBoardId(boardId);
        return ResponseEntity.ok(new CommonResponse("SUCCESS", dto));
    }
}
