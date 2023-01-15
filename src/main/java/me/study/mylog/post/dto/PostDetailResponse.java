package me.study.mylog.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import me.study.mylog.post.entity.Post;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Builder
@AllArgsConstructor
@Getter
public class PostDetailResponse {
    private Long id;
    private Long boardId;
    private String boardName;
    private String boardUri;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String content;
    private Long views;
    private String email;
    private String authorName;
    private Set<String> hashtagList;
    private LocalDateTime modifiedAt;

    public PostDetailResponse(Post entity) {
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
        views = entity.getViews();
        modifiedAt = entity.getModifiedAt();
        hashtagList = entity.getHashtagList();
    }

    private String toStringDateTime(LocalDateTime localDateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return Optional.ofNullable(localDateTime)
                .map(formatter::format)
                .orElse("");
    }
}
