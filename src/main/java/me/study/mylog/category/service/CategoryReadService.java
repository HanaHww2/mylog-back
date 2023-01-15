package me.study.mylog.category.service;

import lombok.RequiredArgsConstructor;
import me.study.mylog.category.entity.Category;
import me.study.mylog.category.repository.CategoryRepository;
import me.study.mylog.category.dto.CategoryResponseDto;
import me.study.mylog.category.mapper.CategoryMapper;
import me.study.mylog.common.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryReadService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly=true)
    public List<CategoryResponseDto> getCategoriesByBoardId(Long boardId) {

        return categoryRepository.findByBoardIdOrderByCreatedAt(boardId)
                .stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly=true)
    public CategoryResponseDto getCategoryDetailByIdAndBoardId(Long categoryId, Long boardId) {

        var category = categoryRepository.findByIdAndBoardId(categoryId, boardId)
                .orElseThrow(() -> {throw new BadRequestException("Not valid category Id");});
        return CategoryMapper.toDto(category);
    }

    @Transactional(readOnly=true)
    public Category getCategoryByIdAndBoardId(Long categoryId, Long boardId) {
        return categoryRepository.findByIdAndBoardId(categoryId, boardId)
                .orElseThrow(()->{throw new BadRequestException("Not Valid Request");});
    }
}
