package me.study.mylog.category;

import lombok.AllArgsConstructor;
import me.study.mylog.auth.security.UserPrincipal;
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
@RequestMapping("/api")
public class CategoryController {

//    CategoryService categoryService;
//    @GetMapping("/v1/categories")
//    public ResponseEntity<?> getCategoriesByUser(Principal principal) {
//        List<CategoryResponseDto> dto = categoryService.getCategoriesByUserEmail(principal.getName());
//        return new ResponseEntity<>(dto, HttpStatus.OK);
//    }
}
