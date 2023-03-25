package me.study.mylog.usecase;

import lombok.RequiredArgsConstructor;
import me.study.mylog.board.entity.BoardMember;
import me.study.mylog.board.entity.BoardMemberType;
import me.study.mylog.board.service.BoardMemberReadService;
import me.study.mylog.category.dto.CategoryModifyRequest;
import me.study.mylog.category.dto.CategoryResponseDto;
import me.study.mylog.category.service.CategoryWriteService;
import me.study.mylog.common.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ModifyCategoryValueUsecase {

    private final BoardMemberReadService boardMemberReadService;
    private final CategoryWriteService categoryWriteService;

    @Transactional
    public CategoryResponseDto execute(CategoryModifyRequest request, Long userId) {

        var boardMember = boardMemberReadService.getBoardMemberByBoardIdAndUserId(request.boardId(), userId);
        if (!Objects.equals(boardMember.getBoardMemberType(), BoardMemberType.MANAGER)
                || !Objects.equals(boardMember.getBoardMemberType(), BoardMemberType.GENERAL)) {
            throw new BadRequestException("Doesn't have authority");
        }

        return categoryWriteService.modifyCategory(request);
    }

}

