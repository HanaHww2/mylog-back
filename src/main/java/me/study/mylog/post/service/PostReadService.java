package me.study.mylog.post.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.study.mylog.common.dto.PageResponse;
import me.study.mylog.post.repository.PostRepository;
import me.study.mylog.post.entity.Post;
import me.study.mylog.post.dto.PostDetailResponse;
import me.study.mylog.post.dto.PostMainResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostReadService {

    private final PostRepository postRepository;

    /**
     * 게시글 단 건 상세 조회, usecase 용
     */
    @Transactional(readOnly = true)
    public Post findPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 글이 존재하지 않습니다."));
    }

    /**
     * 게시글 단 건 상세 조회
     */
    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetailInfoById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 글이 존재하지 않습니다."));

        return new PostDetailResponse(post);
    }


    /**
     * 특정 게시판 내 전체 게시글 조회
     */
    /* readOnly 사용시, 트랜잭션 범위는 유지하되, 조회 기능만 남겨두어 조회 속도 개선 */
    @Transactional(readOnly=true)
    public PageResponse<PostMainResponse> getAllPostDescByBoardId(Long boardId, Pageable pageable) {
        Page<Post> postPagingList = postRepository.findAllByBoardId(boardId, pageable);
        Page<PostMainResponse> dtoList = postPagingList.map(Post::toDto);

        return new PageResponse(dtoList);
    }

    // 특정 카테코리 게시글 조회
    @Transactional(readOnly=true)
    public PageResponse<PostMainResponse> getAllPostDescByCategoryId(Long categoryId, Pageable pageable) {
        Page<Post> postPagingList = postRepository.findAllByCategoryId(categoryId, pageable);
        Page<PostMainResponse> dtoList = postPagingList.map(Post::toDto);

        return new PageResponse(dtoList);
    }

    // 기본 전체 게시글 조회
    @Transactional(readOnly = true)
    public PageResponse<PostMainResponse> getAllPostDesc(Pageable pageable) {
        Page<PostMainResponse> dtoList = postRepository.findAll(pageable).map(Post::toDto);

        return new PageResponse(dtoList);
    }

    // 추천 게시글 조회


}
