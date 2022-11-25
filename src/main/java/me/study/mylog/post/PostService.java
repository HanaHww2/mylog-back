package me.study.mylog.post;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.study.mylog.board.domain.Board;
import me.study.mylog.board.BoardRepository;
import me.study.mylog.category.Category;
import me.study.mylog.category.CategoryRepository;
import me.study.mylog.post.domain.Post;
import me.study.mylog.upload.domain.ImageFile;
import me.study.mylog.upload.repository.ImageFileRepository;
import me.study.mylog.users.domain.User;
import me.study.mylog.users.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;
    private final ImageFileRepository imageFileRepository;
    private final UserRepository userRepository;

    /*
    * 글 등록과 동시에 글에 첨부된 이미지 등 파일의 저장도 실행한다.
    * */
    @Transactional
    public PostDetailResponseDto save(PostSaveRequestDto dto, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(()-> new IllegalArgumentException("Invalid User Email, couldn't find user"));
        Board board = boardRepository.findById(dto.getBoardId())
                .orElseThrow(()-> new IllegalArgumentException("Invalid Board Id, couldn't find board"));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(()-> new IllegalArgumentException("Invalid Category Id, couldn't find category"));

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .hashtagList(dto.getTagList().toString())
                .board(board)
                .category(category)
                .user(user)
                .build();

        Post saved = postRepository.save(post);

        Optional<List<ImageFileRequestDto>> imageListDto = Optional.ofNullable(dto.getImageListDto());
        if (imageListDto.isEmpty()) return new PostDetailResponseDto(saved);

        List<ImageFile> imageFileList = imageListDto.get()
                .stream()
                .map(file -> {
                    ImageFile imageFile = file.toEntity(saved);
                    imageFile.makeRelImgFileToPost();
                    return imageFile;
                })
                .collect(Collectors.toList());
        imageFileRepository.saveAll(imageFileList);

        return new PostDetailResponseDto(saved);
    }

    /**
     * 게시글 단 건 상세 조회
     * @param id
     * @return
     */
    @Transactional
    public PostDetailResponseDto getPostDetailInfoById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("찾는 글이 존재하지 않습니다."));

        // 조횟수 추가 (변경 감지 작동으로 자동 업데이트)
        Long count = post.addViewCount();
        return new PostDetailResponseDto(post);
    }

    /**
     * 게시글 조회수 업데이트 (리턴 없이 업데이트만 수행)
     * @param id
     * @return
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

    /**
     * 특정 게시판 내 전체 게시글 조회
     */
    /* readOnly 사용시, 트랜잭션 범위는 유지하되, 조회 기능만 남겨두어 조회 속도 개선 */
    @Transactional(readOnly=true)
    public List<PostMainResponseDto> getAllPostDescByBoardId(Long boardId, Pageable pageable) {
        return postRepository.findAllByBoardId(boardId, pageable)
                .stream()
                .map(PostMainResponseDto::new)
                .collect(Collectors.toList());
    }

    // 특정 카테코리 게시글 조회
    @Transactional(readOnly=true)
    public List<PostMainResponseDto> getAllPostDescByCategoryId(Long categoryId, Pageable pageable) {
        return postRepository.findAllByCategoryId(categoryId, pageable)
                .stream()
                .map(PostMainResponseDto::new)
                .collect(Collectors.toList());
    }

    // 기본 전체 게시글 조회
    public List<PostMainResponseDto> getAllPostDesc(Pageable pageable) {
        return postRepository.findAll(pageable)
                .stream()
                .map(PostMainResponseDto::new)
                .collect(Collectors.toList());
    }

    // 추천 게시글 조회


}
