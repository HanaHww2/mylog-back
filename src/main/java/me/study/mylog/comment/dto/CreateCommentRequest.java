package me.study.mylog.comment.dto;

import lombok.*;
import me.study.mylog.upload.dto.ImageFileRequest;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;

/*
* Controller에서 @RequestBody로 외부에서 데이터를 받는 경우엔
* 기본생성자 + setter를 통해서만 값이 할당된다.
* setter가 없는 경우에도 값이 할당되긴 하지만, 내부적으로 리플렉션 등을 이용해
* setter를 생성해내는 작업이 수행되므로,
* 결국 더 비용이 드는 작업이 되는 것 같다.(참조 링크: TODO )
* */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCommentRequest {

    @NotNull private Long boardId;
    @NotNull private Long postId;
    @Nullable private Long parentCommentId;
    @NotNull private String writerName;
    @NotNull private String content;
    private List<ImageFileRequest> imageListDto;
}
