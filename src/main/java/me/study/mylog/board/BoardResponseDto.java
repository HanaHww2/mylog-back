package me.study.mylog.board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.study.mylog.category.Category;
import me.study.mylog.category.CategoryResponseDto;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BoardResponseDto {
    private Long id;
    private String name;
    private String icon;
    private String uri;
    private List<CategoryResponseDto> categories;

    public BoardResponseDto(Board board) {
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
