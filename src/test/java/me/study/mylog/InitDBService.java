package me.study.mylog;

import lombok.RequiredArgsConstructor;
import me.study.mylog.board.Board;
import me.study.mylog.board.BoardRepository;
import me.study.mylog.category.Category;
import me.study.mylog.category.CategoryRepository;
import me.study.mylog.post.domain.Post;
import me.study.mylog.post.PostRepository;
import me.study.mylog.users.domain.RoleType;
import me.study.mylog.users.domain.User;
import me.study.mylog.users.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Profile("test")
@Service
@RequiredArgsConstructor
public class InitDBService {
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;

    List<User> userList;
    List<Board> boardList;
    List<Category> categoryList;
    List<Post> postList;


    public void initDBForIntegration() {
        this.userList = IntStream.range(0, 3)
                .mapToObj( i -> makeUser("%%"+ i))
                .collect(Collectors.toList());

        userRepository.saveAll(userList);

        this.boardList = IntStream.range(0, 3)
                .mapToObj(i -> makeBoard("--"+i))
                .collect(Collectors.toList());

        boardRepository.saveAll(boardList);

        this.categoryList = IntStream.range(0, 9)
                .mapToObj(i -> makeCategory("$$"+i, userList.get(i % 3), boardList.get(i%3)))
                .collect(Collectors.toList());

        categoryRepository.saveAll(categoryList);

        this.postList = IntStream.range(0, 30)
                .mapToObj( i -> makePost("***" + i, userList.get(i % 3), categoryList.get(i%6)))
                .collect(Collectors.toList());

        postRepository.saveAll(postList);
    }

    User makeUser(String randomStr) {
        return User.builder()
                .email("test"+ randomStr+"@example.com")
                .password("password" + randomStr)
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
                .user(user)
                .board(board)
                .name("name"+ randomStr)
                .build();
    }
    Post makePost(String randomStr, User user, Category category) {
        return Post.builder()
                .title("title" + randomStr)
                .content("content" + randomStr)
                .category(category)
                .user(user)
                .build();
    }

}
