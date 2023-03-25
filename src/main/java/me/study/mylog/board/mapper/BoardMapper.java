package me.study.mylog.board.mapper;

import me.study.mylog.board.dto.BoardDetailResponse;
import me.study.mylog.board.dto.CreateBoardRequest;
import me.study.mylog.board.entity.Board;
import me.study.mylog.category.dto.CategoryResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BoardMapper {

    public static Board toEntity(CreateBoardRequest createBoardRequest) {
        return Board.builder()
                .name(createBoardRequest.name())
                .uri("/" + createBoardRequest.name())
                .views(0L)
                .boardType(createBoardRequest.boardType())
                .icon(createBoardRequest.icon())
                .build();
    }

    public static BoardDetailResponse toBoardDetailResponseDto(Board board, List<CategoryResponseDto> categoryResponseDtoList) {

        return BoardDetailResponse.builder()
                .id(board.getId())
                .name(board.getName())
                .uri(board.getUri())
                .views(board.getViews())
                .icon(board.getIcon())
                .categories(categoryResponseDtoList)
                .build();
    }
}
