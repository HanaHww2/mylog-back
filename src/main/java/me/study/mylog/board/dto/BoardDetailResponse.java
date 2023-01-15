package me.study.mylog.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.study.mylog.board.entity.BoardMemberType;
import me.study.mylog.category.dto.CategoryResponseDto;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDetailResponse {
    private Long id;
    private String name;
    private String uri;
    private Long views;
    private List<CategoryResponseDto> categories; // 카테고리 정보를 포함한 응답값
    private String icon;
    private String nickname;
    private BoardMemberType boardMemberType;

// TODO 중첩 클래스 구조로 변경 및 record 활용 가능한지 확인
//    public class BoardMember {
//        private String nickname;
//        private BoardMemberType boardMemberType;
//    }

}
