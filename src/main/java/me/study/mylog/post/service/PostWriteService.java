package me.study.mylog.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.study.mylog.board.repository.BoardRepository;
import me.study.mylog.category.repository.CategoryRepository;
import me.study.mylog.post.repository.PostRepository;
import me.study.mylog.post.dto.CreatePostRequest;
import me.study.mylog.post.entity.Post;
import me.study.mylog.upload.repository.ImageFileRepository;
import me.study.mylog.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostWriteService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;
    private final ImageFileRepository imageFileRepository;
    private final UserRepository userRepository;

    /*
     * 글 등록과 동시에 하나의 트랜잭션에서
     * TODO 글에 첨부된 이미지 등 파일 정보의 저장도 실행한다. -> 메소드 분리하기!!!
     * */
    @Transactional
    public Post save(CreatePostRequest dto, Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(()-> new IllegalArgumentException("Invalid User Email, couldn't find user"));
//        Board board = boardRepository.findById(dto.getBoardId())
//                .orElseThrow(()-> new IllegalArgumentException("Invalid Board Id, couldn't find board"));
//        Category category = categoryRepository.findById(dto.getCategoryId())
//                .orElseThrow(()-> new IllegalArgumentException("Invalid Category Id, couldn't find category"));

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .hashtagList(dto.getTagList())
//                .board(board)
//                .category(category)
//                .user(user)
                .boardId(dto.getBoardId())
                .categoryId(dto.getCategoryId())
                .userId(userId)
                .build();

        Post saved = postRepository.save(post);
        return saved;
    }

    /**
     * 게시글 소프트 딜리트
     */
    @Transactional
    public void delete(Post post) {
        postRepository.delete(post);
    }

    /**
     * 게시글 조회수 업데이트 (리턴 없이 업데이트만 수행)
     */
    @Transactional
    public Long updatePostViewsById(Long id) {
        // 게시글 조회
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("찾는 글이 존재하지 않습니다."));

        // 조횟수 추가 (변경 감지 작동으로 자동 업데이트)
        Long counting = post.addViewCount();
        log.info("+++++++++{}+++++++++", counting.toString());
        return counting;
    }

}
