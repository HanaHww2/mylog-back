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
    private String boardName;
    private String boardUri;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String content;
    private Integer readingCount;

    private String email;
    private String authorName;
    private String hashtagList;
    private LocalDateTime modifiedDate;

    public PostDetailResponseDto(Post entity) {
        id = entity.getId();
        boardId = entity.getBoard().getId();
        boardName = entity.getBoard().getName();
        boardUri = entity.getBoard().getUri();
        categoryId = entity.getCategory().getId();
        categoryName = entity.getCategory().getName();
        title = entity.getTitle();
        email = entity.getUser().getEmail();
        authorName = entity.getUser().getName();
        content = entity.getContent();
        readingCount = entity.getReadingCount();
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
