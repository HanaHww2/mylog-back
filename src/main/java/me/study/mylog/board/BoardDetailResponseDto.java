package me.study.mylog.board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.study.mylog.board.domain.Board;
import me.study.mylog.board.domain.BoardMember;
import me.study.mylog.board.domain.BoardMemberType;
import me.study.mylog.category.Category;
import me.study.mylog.category.CategoryResponseDto;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BoardDetailResponseDto {
    private Long id;
    private String name;
    private String icon;
    private String uri;
    private List<CategoryResponseDto> categories; // 카테고리 정보를 포함한 응답값

    private String nickname;
    private BoardMemberType boardMemberType;

    public BoardDetailResponseDto(BoardMember boardMember) {
        Board board = boardMember.getBoard();

        this.id = board.getId();
        this.name = board.getName();
        this.icon = board.getIcon();
        this.uri = board.getUri();

        this.nickname = boardMember.getNickname();
        this.boardMemberType = boardMember.getBoardMemberType();

        List<Category> categories = board.getCategories();
        this.categories = categories
                .stream()
                .map(item -> new CategoryResponseDto(item, 2))
                .collect(Collectors.toList());
    }

    public BoardDetailResponseDto(Board board) {

        this.id = board.getId();
        this.name = board.getName();
        this.icon = board.getIcon();
        this.uri = board.getUri();

        List<Category> categories = board.getCategories();
        this.categories = categories
                .stream()
                .map(item -> new CategoryResponseDto(item, 2))
                .collect(Collectors.toList());
    }

}
