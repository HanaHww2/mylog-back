package me.study.mylog.usecase;

import lombok.RequiredArgsConstructor;
import me.study.mylog.upload.dto.ImageFileRequest;
import me.study.mylog.post.dto.PostDetailResponse;
import me.study.mylog.post.dto.CreatePostRequest;
import me.study.mylog.post.entity.Post;
import me.study.mylog.post.service.PostWriteService;
import me.study.mylog.upload.service.ImageFileWriteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CreatePostUsecase {
    private final PostWriteService postWriteService;
    private final ImageFileWriteService imageFileWriteService;

    @Transactional
    public PostDetailResponse execute(CreatePostRequest dto, Long userId) {

        Post saved = postWriteService.save(dto, userId);
        Optional<List<ImageFileRequest>> imageDtoList = Optional.ofNullable(dto.getImageListDto());

        if (imageDtoList.isEmpty()) return new PostDetailResponse(saved);
        imageFileWriteService.saveFileList(imageDtoList.get(), saved);

        return new PostDetailResponse(saved);
    }

}
