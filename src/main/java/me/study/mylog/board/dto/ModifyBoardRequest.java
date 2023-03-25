package me.study.mylog.board.dto;

import lombok.Builder;
import me.study.mylog.board.entity.BoardType;

public record ModifyBoardRequest(
        Long boardId,
        String name,
        String icon,
        BoardType boardType
) {
    @Builder
    public ModifyBoardRequest {}
}
