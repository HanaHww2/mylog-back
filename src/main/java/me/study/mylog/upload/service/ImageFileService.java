package me.study.mylog.upload.service;

import lombok.RequiredArgsConstructor;
import me.study.mylog.upload.repository.ImageFileRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ImageFileService {
    private final ImageFileRepository imageFileRepository;

/*
    @Transactional
    public ImageFileResponseDto save(ImageFileRequestDto dto) {

        return ImageFileResponseDto.builder().build();
    }*/
}
