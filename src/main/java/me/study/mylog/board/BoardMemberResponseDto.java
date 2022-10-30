package me.study.mylog.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.study.mylog.board.domain.BoardMemberType;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardMemberResponseDto {
    private BoardMemberType type;
    private String nickname;
}
