package me.study.mylog.board.dto;

import lombok.Builder;
import me.study.mylog.board.entity.BoardType;

public record CreateBoardRequest(
        String name,
        String uri,
        String icon,
        BoardType boardType,
        String nickname // 랜덤?ㅠ
) {
    @Builder
    public CreateBoardRequest {}
}
