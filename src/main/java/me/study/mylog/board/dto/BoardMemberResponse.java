package me.study.mylog.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.study.mylog.board.entity.BoardMemberType;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardMemberResponse {
    private BoardMemberType type;
    private String nickname;
}
