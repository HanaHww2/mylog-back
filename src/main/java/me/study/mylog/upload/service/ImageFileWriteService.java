package me.study.mylog.upload.service;

import lombok.RequiredArgsConstructor;
import me.study.mylog.post.dto.ImageFileRequest;
import me.study.mylog.post.entity.Post;
import me.study.mylog.upload.domain.ImageFile;
import me.study.mylog.upload.dto.ImageFileResponseDto;
import me.study.mylog.upload.repository.ImageFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ImageFileWriteService {
    private final ImageFileRepository imageFileRepository;

    @Transactional
    public void saveFileList(List<ImageFileRequest> imageDtoList, Post saved) {
        List<ImageFile> imageFileList = imageDtoList.stream()
                .map(file -> {
                    ImageFile imageFile = file.toEntity(saved);
                    imageFile.makeRelImgFileToPost();
                    return imageFile;
                })
                .collect(Collectors.toList());
        imageFileRepository.saveAll(imageFileList);
    }
}
