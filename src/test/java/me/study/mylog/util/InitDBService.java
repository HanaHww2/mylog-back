package me.study.mylog.util;

import lombok.RequiredArgsConstructor;
import me.study.mylog.board.entity.Board;
import me.study.mylog.board.entity.BoardMember;
import me.study.mylog.board.entity.BoardMemberType;
import me.study.mylog.board.repository.BoardMemberRepository;
import me.study.mylog.board.repository.BoardRepository;
import me.study.mylog.category.entity.Category;
import me.study.mylog.category.repository.CategoryRepository;
import me.study.mylog.post.entity.Post;
import me.study.mylog.post.PostRepository;
import me.study.mylog.users.domain.RoleType;
import me.study.mylog.users.domain.User;
import me.study.mylog.users.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Profile("test")
@RequiredArgsConstructor
@Service
public class InitDBService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;

    private List<User> userList;
    private List<Board> boardList;
    private List<BoardMember> boardMemberList;
    private List<Category> categoryList;
    private List<Post> postList;

    public void initDBWithOneUser() {

    }

    @Transactional
    public void initDBForIntegration() {
        this.userList = IntStream.range(0, 3)
                .mapToObj( i -> makeUser("%%"+ i))
                .collect(Collectors.toList());

        userRepository.saveAll(userList);

        this.boardList = IntStream.range(0, 3)
                .mapToObj(i -> makeBoard("--"+i))
                .collect(Collectors.toList());

        boardRepository.saveAll(boardList);

        this.boardMemberList = IntStream.range(0, 3)
                .mapToObj(i -> makeBoardMember("--"+i, i))
                .collect(Collectors.toList());

        boardMemberRepository.saveAll(boardMemberList);

        this.categoryList = IntStream.range(0, 9)
                .mapToObj(i -> makeCategory("$$"+i, userList.get(i % 3), boardList.get(i%3)))
                .collect(Collectors.toList());

        categoryRepository.saveAll(categoryList);

        this.postList = IntStream.range(0, 30)
                .mapToObj( i -> makePost("***" + i, userList.get(i % 3), categoryList.get(i%6), boardList.get(i%3)))
                .collect(Collectors.toList());

        postRepository.saveAll(postList);
    }

    private BoardMember makeBoardMember(String randomStr, int i) {
        return BoardMember.builder()
                .nickname("nick"+randomStr)
                .boardMemberType(BoardMemberType.GENERAL)
                .user(userList.get(i))
                .board(boardList.get(i))
                .build();
    }


    User makeUser(String randomStr) {
        return User.builder()
                .email("test"+ randomStr+"@example.com")
                .password(passwordEncoder.encode("password" + randomStr))
                .name("name"+ randomStr)
                .role(RoleType.USER)
                .build();
    }

    Board makeBoard(String randomStr) {
        return Board.builder()
                .name("name" + randomStr)
                .uri("/uri/" + randomStr)
                .build();
    }

    Category makeCategory(String randomStr, User user, Board board) {
        return Category.builder()
                //.user(user)
                .board(board)
                .name("name"+ randomStr)
                .build();
    }
    
    // TODO 보드 정보도 등록해야 함
    Post makePost(String randomStr, User user, Category category, Board board) {
        return Post.builder()
                .title("title" + randomStr)
                .content("content" + randomStr)
                .category(category)
                .user(user)
                .board(board)
                .build();
    }

}
