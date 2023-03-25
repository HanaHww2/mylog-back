package me.study.mylog.comment.dto;

import lombok.*;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModifyCommentRequest {
    @NotNull private Long commentId;
    @NotNull private String content;
    @Nullable private Long parentCommentId;
}
