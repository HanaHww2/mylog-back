package me.study.mylog.post;

import me.study.mylog.board.entity.Board;
import me.study.mylog.board.repository.BoardRepository;
import me.study.mylog.board.entity.BoardType;
import me.study.mylog.category.entity.Category;
import me.study.mylog.category.repository.CategoryRepository;
import me.study.mylog.post.entity.Post;
import me.study.mylog.post.dto.PostDetailResponse;
import me.study.mylog.post.dto.SavePostRequest;
import me.study.mylog.post.service.PostReadService;
import me.study.mylog.post.service.PostWriteService;
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

import java.util.Optional;
import java.util.Set;

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
    private PostReadService postReadService;
    @InjectMocks
    private PostWriteService postWriteService;

    User user;
    Board board;
    Category category;

    @BeforeEach
    void setUp() {
        this.user = User.builder()
                .id(1L)
                .email("test@example.com")
                .build();
        this.board = Board.builder()
                .id(1L)
                .uri("/mylog")
                .boardType(BoardType.DEFAULT)
                .build();
        this.category = Category.builder()
                .id(2L)
                .board(board)
                .name("test")
                .build();
    }

    @DisplayName("Dto_데이터가_MYLOG_테이블에_잘_저장되는지_검증")
    @Test
    public void chkDtoDataSavedWell() {
        //given
        Long userId = 1L;
        String userEmail = "test@example.com";

        SavePostRequest requestDto = SavePostRequest.builder()
//                .email("hanah@example.com")
                .content("테스트 본문")
                .title("테스트 타이틀")
                .boardId(1l)
                .categoryId(2l)
                .build();

        Post post = Post.builder()
                .id(3L)
                .user(user)
                .board(board)
                .category(category)
                .title("테스트 타이틀")
                .content("테스트 본문")
                .hashtagList(Set.of("1", "2", "3"))
               // .imageFileList()
                        .build();

//        given(userRepository.findById(userId)).willReturn(Optional.of(this.user));
//        given(boardRepository.findById(requestDto.getBoardId())).willReturn(Optional.ofNullable(this.board));
//        given(categoryRepository.findById(requestDto.getCategoryId())).willReturn(Optional.ofNullable(this.category));
        given(postRepository.save(any())).willReturn(post);

        //when
        Post saved = postWriteService.save(requestDto, userId);
        System.out.println(saved.getHashtagList());

        //then
        Assertions.assertThat(saved.getContent()).isEqualTo(requestDto.getContent());
        Assertions.assertThat(saved.getTitle()).isEqualTo(requestDto.getTitle());
    }
}