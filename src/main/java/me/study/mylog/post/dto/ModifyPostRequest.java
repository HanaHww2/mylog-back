package me.study.mylog.post.dto;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModifyPostRequest {
    private Long postId;
    private Long boardId;
    private Long categoryId;
    private String title;
    private String content;
    @Builder.Default
    private Set<String> tagList = new HashSet<>();
}
