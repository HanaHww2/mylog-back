package me.study.mylog.post;

import lombok.Getter;
import me.study.mylog.post.domain.Post;

import java.time.LocalDateTime;

@Getter
public class PostMainResponseDto {
    private Long id;
    private String email;
    private String authorName;
    private String title;
    private String content;
    private LocalDateTime modifiedDate;


    public PostMainResponseDto(Post entity) {
        id = entity.getId();
        email = entity.getUser().getEmail();
        authorName = entity.getUser().getName();
        title = entity.getTitle();
        content = entity.getContent();
        modifiedDate = entity.getModifiedDate();
    }
}
