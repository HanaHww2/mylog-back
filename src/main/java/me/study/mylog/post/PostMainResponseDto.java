package me.study.mylog.post;

import lombok.Getter;
import me.study.mylog.post.domain.Post;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
public class PostMainResponseDto {
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
    private String hashtagList;
    private LocalDateTime modifiedDate;


    public PostMainResponseDto(Post entity) {
        id = entity.getId();
        boardId = entity.getBoard().getId();
        boardName = entity.getBoard().getName();
        boardUri = entity.getBoard().getUri();
        title = entity.getTitle();
        email = entity.getUser().getEmail();
        authorName = entity.getUser().getName();
        content = entity.getContent();
        modifiedDate = entity.getModifiedDate();
        hashtagList = entity.getHashtagList();

        if (Optional.ofNullable(entity.getCategory()).isPresent()) {
            categoryId = entity.getCategory().getId();
            categoryName = entity.getCategory().getName();
        }

    }
}
