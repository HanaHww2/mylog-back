package me.study.mylog.post.dto;

import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class SavePostRequest {
    // 사용자 정보는 토큰으로 확인한다.
    //private String email;
    private Long boardId;
    private Long categoryId;
    private String title;
    private String content;
    @Builder.Default
    private Set<String> tagList = new HashSet<>();
    private List<ImageFileRequest> imageListDto;
}
