package me.study.mylog.board.mapper;

import me.study.mylog.board.entity.Board;
import me.study.mylog.board.dto.BoardDetailResponse;
import me.study.mylog.board.dto.BoardMemberResponse;
import me.study.mylog.board.entity.BoardMember;
import me.study.mylog.board.entity.BoardMemberType;
import me.study.mylog.category.dto.CategoryResponseDto;
import me.study.mylog.users.domain.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BoardMemberMapper {

    public static BoardMemberResponse toBoardMemberResponseDto (BoardMember boardMember) {
        return BoardMemberResponse.builder()
                .nickname(boardMember.getNickname())
                .type(boardMember.getBoardMemberType())
                .build();
    }

    public static BoardDetailResponse toBoardDetailResponseDto(BoardMember boardMember,
                                                               Board board, List<CategoryResponseDto> categoryDtoList) {

        return BoardDetailResponse.builder()
                .id(board.getId())
                .name(board.getName())
                .uri(board.getUri())
                .categories(categoryDtoList)
                .icon(board.getIcon())
                .nickname(boardMember.getNickname())
                .boardMemberType(boardMember.getBoardMemberType())
                .build();
    }

    public static BoardMember toEntity(Board board, User user, BoardMemberType boardMemberType, String nickname) {
        return BoardMember.builder()
                .board(board)
                .user(user)
                .boardMemberType(boardMemberType)
                .nickname(nickname)
                .build();
    }
}
