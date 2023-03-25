package me.study.mylog.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
public class CommentDetailResponse {
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

    private String toStringDateTime(LocalDateTime localDateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return Optional.ofNullable(localDateTime)
                .map(formatter::format)
                .orElse("");
    }
}
