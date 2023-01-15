package me.study.mylog.usecase;

import lombok.RequiredArgsConstructor;
import me.study.mylog.board.service.BoardMemberReadService;
import me.study.mylog.category.dto.CategoryResponseDto;
import me.study.mylog.category.service.CategoryReadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class GetCategoryDetailUsecase {

    private final BoardMemberReadService boardMemberReadService;
    private final CategoryReadService categoryReadService;

    @Transactional
    public CategoryResponseDto execute(Long boardId, Long categoryId, Long userId) {

        boardMemberReadService.existsBoardMemberByBoardIdAndUserId(boardId, userId);
        var response = categoryReadService.getCategoryDetailByIdAndBoardId(categoryId, boardId);

        return response;
    }

}
