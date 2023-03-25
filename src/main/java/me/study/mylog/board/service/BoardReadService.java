package me.study.mylog.board.service;

import lombok.RequiredArgsConstructor;
import me.study.mylog.board.dto.BoardDetailResponse;
import me.study.mylog.board.mapper.BoardMapper;
import me.study.mylog.board.repository.BoardRepository;
import me.study.mylog.category.mapper.CategoryMapper;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class BoardReadService {
    private final BoardRepository boardRepository;

    public BoardDetailResponse getBoardById(Long boardId) {

        var boardById = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Not valid boardId"));

        var categoryDtoList = boardById.getCategories()
                .stream()
                .map(CategoryMapper::toDto)
                .toList();

        return BoardMapper.toBoardDetailResponseDto(boardById, categoryDtoList);
    }

    public BoardDetailResponse getBoardByUri(String boardUri) {
        var boardByUri = boardRepository.findByUri(boardUri)
                .orElseThrow(() -> new IllegalArgumentException("Not valid boardId"));

        var categoryDtoList = boardByUri.getCategories()
                .stream()
                .map(CategoryMapper::toDto)
                .toList();

        return BoardMapper.toBoardDetailResponseDto(boardByUri, categoryDtoList);
    }
}
