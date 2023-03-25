package me.study.mylog.usecase;

import lombok.RequiredArgsConstructor;
import me.study.mylog.board.entity.BoardMemberType;
import me.study.mylog.board.service.BoardMemberReadService;
import me.study.mylog.category.service.CategoryWriteService;
import me.study.mylog.common.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class DeleteCategoryUsecase {
    private final BoardMemberReadService boardMemberReadService;
    private final CategoryWriteService categoryWriteService;

    @Transactional
    public void execute(Long boardId, Long categoryId, Long userId) {

        var boardMember = boardMemberReadService.getBoardMemberByBoardIdAndUserId(boardId, userId);
        if (!Objects.equals(boardMember.getBoardMemberType(), BoardMemberType.MANAGER)) {
            throw new BadRequestException("Doesn't have authority");
        }
        categoryWriteService.deleteCategory(categoryId);
    }
}
