package me.study.mylog.post;

import lombok.Getter;
import me.study.mylog.post.domain.Post;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Getter
public class PostDetailResponseDto {
    private Long id;
    private Long boardId;
    private Long categoryId;
    private String title;
    private String content;
    private String email;
    private String authorName;
    private String hashtagList;
    private LocalDateTime modifiedDate;

    public PostDetailResponseDto(Post entity) {
        id = entity.getId();
        boardId = entity.getBoard().getId();
        categoryId = entity.getCategory().getId();
        title = entity.getTitle();
        email = entity.getUser().getEmail();
        authorName = entity.getUser().getName();
        content = entity.getContent();
        modifiedDate = entity.getModifiedDate();
        hashtagList = entity.getHashtagList();
    }

    private String toStringDateTime(LocalDateTime localDateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return Optional.ofNullable(localDateTime)
                .map(formatter::format)
                .orElse("");
    }
}
