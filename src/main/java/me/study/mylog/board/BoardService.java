package me.study.mylog.board;

import lombok.RequiredArgsConstructor;
import me.study.mylog.category.CategoryResponseDto;
import me.study.mylog.users.domain.User;
import me.study.mylog.users.repository.UserRepository;
import me.study.mylog.users.service.UserService;
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


    public List<BoardResponseDto> getBoardsByUserEmail(String userEmail) {
//        User user = userRepository.findByEmail(userEmail)
//                .orElseThrow(() -> new IllegalArgumentException("No Valid User"));
        // 보드 멤버에서 user 기준 사용하는 보드Id를 가져와서 보드 정보를 조회하는 로직으로 수정이 필요하다

        // board 와 category를 한꺼번에 로딩하려는 경우, N+1의 문제가 발생한다.
        // 보드에서 카테고리 리스트를 가져오는 것은 eager 패치와 entitygraph를 사용해 해결할 수 있었으나
        // 카테고리의 자식 카테고리를 가져오는 과정에서 쿼리가 매번 다시 발생한다.
        // 보드마다 카테고리를 가지므로, 보드를 먼저 조회하고 카테고리를 따로 조회하는 방식으로 변경
        // 전체를 한꺼번에 조회하는 jpql 코드도 고려해봐야겠다.
        return boardRepository.findByUserEmail(userEmail)
                .stream()
                .map(BoardResponseDto::new)
                .collect(Collectors.toList());
    }

    // 최초 가입시, 개인 board 생성
    public Board createFirstBoard(User user) {
        String email =  user.getEmail();
        int idx =  user.getEmail().lastIndexOf("@");
        String uri = "/" + user.getEmail().substring(0, idx);

        while (boardRepository.existsBoardByUri(uri)) uri += "-" + UUID.randomUUID();

        Board board = Board.builder()
                .name("MyLog")
                .uri(uri)
                .user(user)
                .boardType(BoardType.DEFAULT)
                .build();

//        BoardMember boardMember = BoardMember.builder()
//                .user(user)
//                .board(board)
//                .memberType(MemberType.MANAGER)
//                .build();

        Board savedBoard = boardRepository.save(board);
        //boardMemberRepository.save(boardMember);
        return savedBoard;
    }

}
