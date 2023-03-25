package me.study.mylog.board.service;

import lombok.RequiredArgsConstructor;
import me.study.mylog.board.dto.BoardDetailResponse;
import me.study.mylog.board.entity.BoardMember;
import me.study.mylog.board.mapper.BoardMemberMapper;
import me.study.mylog.board.repository.BoardMemberRepository;
import me.study.mylog.category.mapper.CategoryMapper;
import me.study.mylog.common.exception.BadRequestException;
import me.study.mylog.users.domain.User;
import me.study.mylog.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BoardMemberReadService {

    private final UserRepository userRepository;
    private final BoardMemberRepository boardMemberRepository;

    @Transactional(readOnly = true)
    public void existsBoardMemberByBoardIdAndUserId(Long boardId, Long userId) {
        var result = boardMemberRepository.existsByBoardIdAndUserId(boardId, userId);
        if (!result) throw new BadRequestException("Not a board member");
    }

    @Transactional(readOnly = true)
    public BoardMember getBoardMemberByBoardIdAndUserId(Long boardId, Long userId) {
        var boardMember = boardMemberRepository.findByBoardIdAndUserId(boardId, userId)
                .orElseThrow(() -> {throw new IllegalArgumentException("Not a board member");});
        return boardMember;
    }

    @Transactional(readOnly = true)
    public List<BoardDetailResponse> getBoardsByUserEmail(String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("No Valid User"));

        // board 와 category를 한꺼번에 로딩하려는 경우, N+1의 문제가 발생한다.
        // 보드에서 카테고리 리스트를 가져오는 것은 eager 패치와 entitygraph를 사용해 해결할 수 있었으나
        // 카테고리의 자식 카테고리를 가져오는 과정에서 쿼리가 매번 다시 발생한다.
        // 보드마다 카테고리를 가지므로, 보드를 먼저 조회하고 카테고리를 따로 조회하는 방식으로 변경.
        // 전체를 한꺼번에 조회하는 jpql 코드도 고려해봐야겠다.

        var boardMemberList = boardMemberRepository.findAllByUserEmail(userEmail);

        // 조회 최적화 필요
        // TODO 조회 쿼리 확인해보기!!
        // + 노션이나 디스코드처럼? 카테고리를 보드별로 조회하고, 관련 글 정보를 함께 가져오는게 나을지도
        return boardMemberList.stream()
                .map(boardMember -> {
                    var board = boardMember.getBoard();
                    var categoryDtoList = board.getCategories()
                            .stream()
                            .map(CategoryMapper::toDto)
                            .collect(Collectors.toList());
                    return BoardMemberMapper.toBoardDetailResponseDto(boardMember, board, categoryDtoList);
                })
                .collect(Collectors.toList());
    }

    public List<BoardDetailResponse> getBoardsByUserId(Long userId) {

        var boardMemberList = boardMemberRepository.findAllByUserId(userId);

        // 조회 최적화 필요
        // TODO 조회 쿼리 확인해보기!!
        // + 노션이나 디스코드처럼? 카테고리를 보드별로 조회하고, 관련 글 정보를 함께 가져오는게 나을지도
        return boardMemberList.stream()
                .map(boardMember -> {
                    var board = boardMember.getBoard();
                    var categoryDtoList = board.getCategories()
                            .stream()
                            .map(CategoryMapper::toDto)
                            .collect(Collectors.toList());
                    return BoardMemberMapper.toBoardDetailResponseDto(boardMember, board, categoryDtoList);
                })
                .collect(Collectors.toList());
    }

}
