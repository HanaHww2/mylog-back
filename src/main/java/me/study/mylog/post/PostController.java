package me.study.mylog.post;

import lombok.RequiredArgsConstructor;
import me.study.mylog.auth.security.UserPrincipal;
import me.study.mylog.common.dto.CommonResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

/**
 *
 */
//@CrossOrigin /* 임시방편, 시큐리티 등을 적용하면 시큐리티 필터링 단계에서 다시 설정을 잡아줘야 한다. */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    /**
     * 게시글 저장
     * @param dto
     * @param userPrincipal
     * @return
     */
    @PostMapping("/v1/posts")
    public ResponseEntity<?> savePost(@RequestBody PostSaveRequestDto dto, Principal Principal) {

        PostDetailResponseDto responseDto = postService.save(dto, Principal.getName());

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/id")
                .build()
                .toUri();

        return ResponseEntity.created(location)
                .body(new CommonResult<>("success", responseDto));
    }

    /**
     * 게시글 아이디를 이용해 게시글을 조회하고 디테일 정보를 반환
     * @param postId
     * @return
     */
    @GetMapping("/v1/posts/{postId}")
    public ResponseEntity<?> getPostDetailInfoById(@PathVariable("postId") Long postId) {

        PostDetailResponseDto responseDto = postService.getPostDetailInfoById(postId);
        return ResponseEntity.ok(new CommonResult<>("success", responseDto));
    }


    /**
     * 게시판 id를 이용해 게시판의 전체 게시글 목록 조회, jpa 페이징 기능 활용
     * @param boardId
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/v1/posts/boards/{boardId}")
    public ResponseEntity<?> getAllPostByBoardId(@PathVariable("boardId") Long boardId,
                                                 @RequestParam("page") Integer page, @RequestParam("size") Integer size) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "modifiedDate"));

        List<PostMainResponseDto> dtoList = postService.getAllPostDescByBoardId(boardId, pageRequest);
        return ResponseEntity.ok(new CommonResult<>("success", dtoList));
    }

    /**
     * 전체 게시글 목록 조회, jpa 페이징 기능 활용, 임시 개발용
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/v1/posts")
    public ResponseEntity<?> getAllPosts(@RequestParam(value = "page", required = false, defaultValue = "0") Integer page, @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "modifiedDate"));

        List<PostMainResponseDto> dtoList = postService.getAllPostDesc(pageRequest);
        return ResponseEntity.ok(new CommonResult<>("success", dtoList));
    }
}