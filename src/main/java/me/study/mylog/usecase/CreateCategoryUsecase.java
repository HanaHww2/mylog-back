package me.study.mylog.usecase;

import lombok.RequiredArgsConstructor;
import me.study.mylog.board.entity.BoardMemberType;
import me.study.mylog.board.repository.BoardRepository;
import me.study.mylog.board.service.BoardMemberReadService;
import me.study.mylog.board.service.BoardReadService;
import me.study.mylog.category.dto.CategoryResponseDto;
import me.study.mylog.category.entity.Category;
import me.study.mylog.category.dto.CategoryCreateRequest;
import me.study.mylog.category.service.CategoryReadService;
import me.study.mylog.category.service.CategoryWriteService;
import me.study.mylog.category.mapper.CategoryMapper;
import me.study.mylog.common.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class CreateCategoryUsecase {

    private final BoardReadService boardReadService;
    private final BoardRepository boardRepository;
    private final BoardMemberReadService boardMemberReadService;

    private final CategoryReadService categoryReadService;
    private final CategoryWriteService categoryWriteService;

    @Transactional
    public CategoryResponseDto createNewCategory(CategoryCreateRequest dto, Long userId) {

        var boardMember = boardMemberReadService.getBoardMemberByBoardIdAndUserId(dto.boardId(), userId);
        if (!Objects.equals(boardMember.getBoardMemberType(), BoardMemberType.MANAGER)
                || !Objects.equals(boardMember.getBoardMemberType(), BoardMemberType.GENERAL)) {
            throw new BadRequestException("Doesn't have authority");
        }

        var board = boardRepository.findById(dto.boardId())
                .orElseThrow(() -> { throw new BadRequestException("Not valid boardId");});
        var parentCategory = categoryReadService.getCategoryByIdAndBoardId(dto.categoryId(), dto.boardId());

        Category category = CategoryMapper.toEntity(dto, board, parentCategory);
        categoryWriteService.createNewCategory(category);

        return CategoryMapper.toDto(category);
    }

}
