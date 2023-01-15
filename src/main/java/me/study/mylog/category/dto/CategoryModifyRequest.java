package me.study.mylog.category.dto;

import lombok.Builder;

import javax.validation.constraints.NotNull;

public record CategoryModifyRequest(
        @NotNull Long categoryId,
        @NotNull Long boardId,
        @NotNull String name
) {
    @Builder
    public CategoryModifyRequest {}
}