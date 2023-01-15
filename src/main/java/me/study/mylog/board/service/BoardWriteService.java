package me.study.mylog.board.service;

import lombok.RequiredArgsConstructor;
import me.study.mylog.board.dto.BoardDetailResponse;
import me.study.mylog.board.dto.BoardMemberResponse;
import me.study.mylog.board.dto.CreateBoardRequest;
import me.study.mylog.board.dto.ModifyBoardRequest;
import me.study.mylog.board.entity.Board;
import me.study.mylog.board.entity.BoardMember;
import me.study.mylog.board.entity.BoardMemberType;
import me.study.mylog.board.entity.BoardType;
import me.study.mylog.board.mapper.BoardMapper;
import me.study.mylog.board.mapper.BoardMemberMapper;
import me.study.mylog.board.repository.BoardMemberRepository;
import me.study.mylog.board.repository.BoardRepository;
import me.study.mylog.category.mapper.CategoryMapper;
import me.study.mylog.common.exception.BadRequestException;
import me.study.mylog.users.domain.User;
import me.study.mylog.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BoardWriteService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final BoardMemberRepository boardMemberRepository;

    // 최초 가입시, 개인 board 생성
    @Transactional
    public void createFirstBoard(User user) {
        String email =  user.getEmail();
        int idx =  user.getEmail().lastIndexOf("@");

        // TODO 보드 uri 생성 규칙 짧게 변경!!
        // 버퍼 사용으로 변경 ( 당장 여러 스레드에서 접근할 일은 없더라도 )
        StringBuffer uri = new StringBuffer("/").append(user.getEmail().substring(0, idx));
        String tempUri = uri.toString();
//        String uri = "/" + user.getEmail().substring(0, idx);

        while (boardRepository.existsBoardByUri(tempUri)) {
            tempUri = uri.append('-')
                    .append(UUID.randomUUID())
                    .toString();
        }

        Board board = Board.builder()
                .name("MyLog")
                .uri(tempUri)
                //.user(user)
                .boardType(BoardType.DEFAULT)
                .build();

        Board savedBoard = boardRepository.save(board);
        BoardMemberResponse boardMemberResponse = saveBoardMember(user, savedBoard);
    }

    @Transactional
    public BoardMemberResponse saveBoardMember(User user, Board board) {

        BoardMember bMember = BoardMember.builder()
                .boardMemberType(BoardMemberType.MANAGER) // 디폴트
                .user(user)
                .board(board)
                .nickname(user.getNickname()) // 디폴트
                .build();
        BoardMember saved = boardMemberRepository.save(bMember);

        return BoardMemberResponse.builder()
                .type(BoardMemberType.MANAGER)
                .nickname(bMember.getNickname())
                .build();
    }

    // 오버로딩
    @Transactional
    public BoardMemberResponse saveBoardMember(User user, Board board, BoardMemberType type, String nickname) {

        BoardMember bMember = BoardMember.builder()
                .boardMemberType(type)
                .user(user)
                .board(board)
                .nickname(nickname)
                .build();
        BoardMember saved = boardMemberRepository.save(bMember);

        return BoardMemberMapper.toBoardMemberResponseDto(saved);
    }

    @Transactional
    public BoardDetailResponse createNewBoard(CreateBoardRequest createBoardRequest) {

        var board = BoardMapper.toEntity(createBoardRequest);
        boardRepository.save(board);
        return BoardMapper.toBoardDetailResponseDto(board, Collections.EMPTY_LIST);
    }


    public BoardDetailResponse modifyBoard(ModifyBoardRequest modifyBoardRequest) {

        var board = boardRepository.findById(modifyBoardRequest.boardId())
                .orElseThrow(()->{throw new BadRequestException("Not valid board id");});
        board.modifyValues(modifyBoardRequest);
        var categoryDtoList = board.getCategories()
                .stream()
                .map(CategoryMapper::toDto)
                .toList();
        return BoardMapper.toBoardDetailResponseDto(board, categoryDtoList);
    }
}
