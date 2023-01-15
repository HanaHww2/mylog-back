package me.study.mylog.usecase;

import lombok.RequiredArgsConstructor;
import me.study.mylog.board.service.BoardMemberReadService;
import me.study.mylog.category.dto.CategoryModifyRequest;
import me.study.mylog.category.dto.CategoryResponseDto;
import me.study.mylog.category.service.CategoryWriteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ModifyCategoryValueUsecase {

    private final BoardMemberReadService boardMemberReadService;
    private final CategoryWriteService categoryWriteService;

    @Transactional
    public CategoryResponseDto execute(CategoryModifyRequest request, Long userId) {

        boardMemberReadService.existsBoardMemberByBoardIdAndUserId(request.boardId(), userId);
        var response = categoryWriteService.modifyCategory(request);
        return response;
    }

}

