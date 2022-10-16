package me.study.mylog.board;

import lombok.RequiredArgsConstructor;
import me.study.mylog.board.domain.Board;
import me.study.mylog.board.domain.BoardMember;
import me.study.mylog.board.domain.BoardType;
import me.study.mylog.users.domain.User;
import me.study.mylog.users.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BoardService {
   // private final UserService userService;
   private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final BoardMemberRepository boardMemberRepository;


    public List<BoardDetailResponseDto> getBoardsByUserEmail(String userEmail) {
//        User user = userRepository.findByEmail(userEmail)
//                .orElseThrow(() -> new IllegalArgumentException("No Valid User"));
        // 보드 멤버에서 user 기준 사용하는 보드Id를 가져와서 보드 정보를 조회하는 로직으로 수정이 필요하다

        // board 와 category를 한꺼번에 로딩하려는 경우, N+1의 문제가 발생한다.
        // 보드에서 카테고리 리스트를 가져오는 것은 eager 패치와 entitygraph를 사용해 해결할 수 있었으나
        // 카테고리의 자식 카테고리를 가져오는 과정에서 쿼리가 매번 다시 발생한다.
        // 보드마다 카테고리를 가지므로, 보드를 먼저 조회하고 카테고리를 따로 조회하는 방식으로 변경
        // 전체를 한꺼번에 조회하는 jpql 코드도 고려해봐야겠다.

        //List<BoardMember> allByUserEmail = boardMemberRepository.findAllByUserEmail(userEmail);

        // 조회 최적화 필요
        // + 노션이나 디스코드처럼? 카테고리를 보드별로 조회하고, 관련 글 정보를 함께 가져오는게 나을지도
       return boardMemberRepository.findAllByUserEmail(userEmail).stream()
              .map(BoardDetailResponseDto::new)
               .collect(Collectors.toList());
    }

    public BoardDetailResponseDto getBoardById(Long boardId) {
        return new BoardDetailResponseDto(boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Not valid boardId")));
    }

    // 최초 가입시, 개인 board 생성
    public void createFirstBoard(User user) {
        String email =  user.getEmail();
        int idx =  user.getEmail().lastIndexOf("@");

        // TODO 보드 uri 생성 규칙 변경!!
        String uri = "/" + user.getEmail().substring(0, idx);

        while (boardRepository.existsBoardByUri(uri)) uri += "-" + UUID.randomUUID();

        Board board = Board.builder()
                .name("MyLog")
                .uri(uri)
                //.user(user)
                .boardType(BoardType.DEFAULT)
                .build();

        Board savedBoard = boardRepository.save(board);
        BoardMemberResponseDto boardMemberResponseDto = saveBoardMember(user, savedBoard);
    }

    public BoardMemberResponseDto saveBoardMember(User user, Board board) {

        BoardMember bMember = BoardMember.builder()
                .boardMemberType(BoardMemberType.MANAGER) // 디폴트
                .user(user)
                .board(board)
                .nickname(user.getNickname()) // 디폴트
                .build();
        BoardMember saved = boardMemberRepository.save(bMember);

        return BoardMemberResponseDto.builder()
                .type(BoardMemberType.MANAGER)
                .nickname(bMember.getNickname())
                .build();
    }
    // 오버로딩
    public BoardMemberResponseDto saveBoardMember(User user, Board board, BoardMemberType type, String nickname) {

        BoardMember bMember = BoardMember.builder()
                .boardMemberType(type)
                .user(user)
                .board(board)
                .nickname(nickname)
                .build();
        BoardMember saved = boardMemberRepository.save(bMember);
        
        return BoardMemberResponseDto.builder()
                .type(type)
                .nickname(nickname)
                .build();
    }
}
