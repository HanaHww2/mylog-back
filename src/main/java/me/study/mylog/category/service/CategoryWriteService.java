package me.study.mylog.category.service;

import lombok.RequiredArgsConstructor;
import me.study.mylog.category.dto.CategoryModifyRequest;
import me.study.mylog.category.dto.CategoryResponseDto;
import me.study.mylog.category.entity.Category;
import me.study.mylog.category.mapper.CategoryMapper;
import me.study.mylog.category.repository.CategoryRepository;
import me.study.mylog.common.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CategoryWriteService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponseDto modifyCategory(CategoryModifyRequest dto) {
        var category = categoryRepository.findByIdAndBoardId(dto.categoryId(), dto.boardId())
                .orElseThrow(() -> {throw new BadRequestException("Not valid Request");});
        category.modifyValue(dto);
        return CategoryMapper.toDto(category);
    }

    @Transactional
    public void createNewCategory(Category category) {
        categoryRepository.save(category);
    }

}
