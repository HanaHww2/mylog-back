package me.study.mylog.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@NoArgsConstructor
public class CategoryResponseDto {
    private Long id;
    private String name;
    private String icon;
    private String uri;
    private Long parentId;
    private List<CategoryResponseDto> children;

    public CategoryResponseDto(Category entity, Integer depth) {
        id = entity.getId();
        name = entity.getName();
        uri = entity.getUri();
        parentId = entity.getParent()==null? 0 : entity.getParent().getId();
        if (depth-1 > 0) {
            children = entity.getChildren() == null ? null :
                    entity.getChildren()
                            .stream()
                            .map(child -> new CategoryResponseDto(child, depth-1))
                            .collect(Collectors.toList());
        }
        //    icon = entity.getIcon();

    }

    public CategoryResponseDto(Category entity) {
        Integer depth = 2;
        new CategoryResponseDto(entity, depth);
    }

}
