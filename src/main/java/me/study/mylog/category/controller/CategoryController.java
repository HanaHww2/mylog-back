package me.study.mylog.category.controller;

import lombok.AllArgsConstructor;
import me.study.mylog.auth.security.UserPrincipal;
import me.study.mylog.board.service.BoardMemberReadService;
import me.study.mylog.category.dto.CategoryCreateRequest;
import me.study.mylog.category.dto.CategoryModifyRequest;
import me.study.mylog.category.service.CategoryReadService;
import me.study.mylog.category.dto.CategoryResponseDto;
import me.study.mylog.category.service.CategoryWriteService;
import me.study.mylog.common.dto.ApiResponse;
import me.study.mylog.usecase.CreateCategoryUsecase;
import me.study.mylog.usecase.DeleteCategoryUsecase;
import me.study.mylog.usecase.GetCategoryDetailUsecase;
import me.study.mylog.usecase.ModifyCategoryValueUsecase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class CategoryController {

    private final CategoryReadService categoryReadService;

    private final CreateCategoryUsecase createCategoryUsecase;
    private final ModifyCategoryValueUsecase modifyCategoryValueUsecase;
    private final GetCategoryDetailUsecase getCategoryDetailUsecase;
    private final DeleteCategoryUsecase deleteCategoryUsecase;

    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> createNewCategory(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                              CategoryCreateRequest requestDto) {

        CategoryResponseDto responseDto = createCategoryUsecase.createNewCategory(requestDto, userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse("SUCCESS", responseDto));
    }

    @PutMapping("categories")
    public ResponseEntity<?> modifyCategoryValue(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                 CategoryModifyRequest dto) {

        var responseDto = modifyCategoryValueUsecase.execute(dto, userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse("SUCCESS", responseDto));
    }

    @DeleteMapping("categories/{boardId}/{categoryId}")
    public ResponseEntity<?> deleteCategory(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                            @PathVariable Long boardId, @PathVariable Long categoryId) {
        deleteCategoryUsecase.execute(boardId, categoryId, userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse("SUCCESS", Boolean.TRUE));
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getCategoriesByBoard(@RequestParam Long boardId) {

        List<CategoryResponseDto> dtoList = categoryReadService.getCategoriesByBoardId(boardId);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", dtoList));
    }

    @GetMapping("/categories/{boardId}/{categoryId}")
    public ResponseEntity<?> getCategoryAdminDetailByCategoryId(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                @PathVariable Long boardId, @PathVariable Long categoryId) {

        CategoryResponseDto dto = getCategoryDetailUsecase.execute(boardId, categoryId, userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse("SUCCESS", dto));
    }
}
