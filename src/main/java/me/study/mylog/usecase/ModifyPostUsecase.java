package me.study.mylog.usecase;

import lombok.RequiredArgsConstructor;
import me.study.mylog.board.service.BoardMemberReadService;
import me.study.mylog.common.exception.BadRequestException;
import me.study.mylog.post.PostRepository;
import me.study.mylog.post.dto.ModifyPostRequest;
import me.study.mylog.post.dto.PostDetailResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ModifyPostUsecase {

    private final BoardMemberReadService boardMemberReadService;
    private final PostRepository postRepository;

    @Transactional
    public PostDetailResponse execute(ModifyPostRequest request, Long userId) {

        boardMemberReadService.existsBoardMemberByBoardIdAndUserId(request.getBoardId(), userId);

        var post = postRepository.findByIdAndBoardId(request.getPostId(), request.getBoardId())
                .orElseThrow(()->{throw new BadRequestException("Invalid Request");});
        post.modifyValue(request, userId);

        return new PostDetailResponse(post);
    }
}
