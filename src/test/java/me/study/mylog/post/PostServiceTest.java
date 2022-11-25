package me.study.mylog.post;

import me.study.mylog.board.domain.Board;
import me.study.mylog.board.BoardRepository;
import me.study.mylog.board.domain.BoardType;
import me.study.mylog.category.Category;
import me.study.mylog.category.CategoryRepository;
import me.study.mylog.post.domain.Post;
import me.study.mylog.upload.repository.ImageFileRepository;
import me.study.mylog.users.domain.User;
import me.study.mylog.users.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ImageFileRepository imageFileRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private PostService postService;

    User user;
    Board board;
    Category category;

    @BeforeEach
    void setUp() {
        this.user = User.builder()
                .id(1l)
                .email("test@example.com")
                .build();
        this.board = Board.builder()
                .id(1L)
                .uri("/mylog")
                .boardType(BoardType.DEFAULT)
                .build();
        this.category = Category.builder()
                .id(2l)
                .board(board)
                .name("test")
                .build();
    }

    @Test
    @DisplayName("Dto_데이터가_MYLOG_테이블에_잘_저장되는지_검증")
    public void chkDtoDataSavedWell() {
        //given
        PostSaveRequestDto requestDto = PostSaveRequestDto.builder()
//                .email("hanah@example.com")
                .content("테스트 본문")
                .title("테스트 타이틀")
                .boardId(1l)
                .categoryId(2l)
                .build();
        String userEmail = "test@example.com";
        Post post = Post.builder()
                .id(3L)
                .user(user)
                .board(board)
                .category(category)
                .title("테스트 타이틀")
                .content("테스트 본문")
                .hashtagList(Arrays.asList(new String[]{"one", "two"}).toString())
               // .imageFileList()
                        .build();

        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(this.user));
        given(boardRepository.findById(requestDto.getBoardId())).willReturn(Optional.ofNullable(this.board));
        given(categoryRepository.findById(requestDto.getCategoryId())).willReturn(Optional.ofNullable(this.category));
        given(postRepository.save(any())).willReturn(post);

        //when
        PostDetailResponseDto responseDto = postService.save(requestDto, userEmail);
        System.out.println(responseDto.getHashtagList());

        //then
        Assertions.assertThat(responseDto.getEmail()).isEqualTo(userEmail);
        Assertions.assertThat(responseDto.getContent()).isEqualTo(requestDto.getContent());
        Assertions.assertThat(responseDto.getTitle()).isEqualTo(requestDto.getTitle());
    }
}