package me.study.mylog.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import me.study.mylog.post.entity.Post;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
public class PostMainResponse {
    private Long id;
    private Long boardId;
    private String boardName;
    private String boardUri;
    private Long categoryId;
    private String categoryName;
    private String email;
    private String authorName;
    private String title;
    private String content;
    private Set<String> hashtagList;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime modifiedAt;


    public PostMainResponse(Post entity) {
        id = entity.getId();
        boardId = entity.getBoard().getId();
        boardName = entity.getBoard().getName();
        boardUri = entity.getBoard().getUri();
        title = entity.getTitle();
        email = entity.getUser().getEmail();
        authorName = entity.getUser().getName();
        content = entity.getContent();
        modifiedAt = entity.getModifiedAt();
        hashtagList = entity.getHashtagList();

        if (Optional.ofNullable(entity.getCategory()).isPresent()) {
            categoryId = entity.getCategory().getId();
            categoryName = entity.getCategory().getName();
        }

    }
}
