package me.study.mylog.category.dto;

import lombok.Builder;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;

public record CategoryCreateRequest(
        @NotNull Long categoryId,
        @NotNull String name,
        @NotNull String uri,
        @Nullable Long parentId,
        @NotNull Long boardId
) {
    @Builder
    public CategoryCreateRequest {}
}
