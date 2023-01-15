package me.study.mylog.category.dto;

import lombok.Builder;

import java.util.List;

public record CategoryResponseDto (
     Long id,
     String name,
     String uri,
     Long parentId,
     Long boardId,
     List<CategoryResponseDto> children
) {
    @Builder
    public CategoryResponseDto {}
}
