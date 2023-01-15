package me.study.mylog.category.mapper;

import me.study.mylog.board.entity.Board;
import me.study.mylog.category.entity.Category;
import me.study.mylog.category.dto.CategoryResponseDto;
import me.study.mylog.category.dto.CategoryCreateRequest;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CategoryMapper {

    public static CategoryResponseDto toDto(Category category) {
        Integer depth = 2;
        return toDto(category, depth);
    }

    // TODO 결과 확인 및 메소드 수정
    public static CategoryResponseDto toDto(Category category, Integer depth) {

        List<CategoryResponseDto> categories = Collections.emptyList();
        if (depth-1 > 0) {
            categories =
                    category.getChildren() == null ? null : category.getChildren()
                            .stream()
                            .map(child -> toDto(child, depth-1))
                            .collect(Collectors.toList());
        }

        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .uri(category.getUri())
                .parentId(Objects.isNull(category.getParent()) ? null : category.getParent().getId())
                .children(categories)
                .build();

        //    icon = category.getIcon();
    }

    public static Category toEntity(CategoryCreateRequest CategoryDto, Board board, Category parentCategory) {
        return Category.builder()
                .name(CategoryDto.name())
                .uri(CategoryDto.uri())
                .board(board)
                .parent(parentCategory)
                .build();
    }
}
